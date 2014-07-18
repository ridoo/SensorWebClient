/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.server.sos.connector.hydro;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;
import static org.n52.server.sos.connector.hydro.SOSwithSoapAdapter.GET_DATA_AVAILABILITY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.MetadataHandler;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.parser.ConnectorUtils;
import org.n52.server.parser.GetFeatureOfInterestParser;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosService;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HydroMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HydroMetadataHandler.class);

    public HydroMetadataHandler(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public void assembleTimeseriesMetadata(TimeseriesProperties properties) throws Exception {

        // TODO use different request strategy to obtain metadata/uom when SOS supports HydroProfile
        // (HyProfile must request an Observation (without timestamp we get the last value))
        // ==> move metadata obtaining strategy to MetadataHandler class: a different strategy can
        // be used by overriding the default (metadata via SensorML)

    }

    @Override
    public SOSMetadata performMetadataCompletion() throws Exception {
    	LOGGER.info("Start perform metadata completion");
        SOSMetadata metadata = initMetadata();
        LOGGER.info("init of metadata finished");
        // get a waterml specific responseFormat if set
        String responseFormat = ConnectorUtils.getResponseFormat(getServiceDescriptor(), "waterml");
        if (responseFormat != null) {
            metadata.setOmVersion(responseFormat);
        }
        collectTimeseries(metadata);
        return metadata;
    }

    @Override
    public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
        SOSMetadata newMetadata = metadata.clone();
        initMetadata();
        collectTimeseries(newMetadata);
        return newMetadata;
    }

    protected void collectTimeseries(SOSMetadata metadata) throws OXFException,
            InterruptedException,
            ExecutionException,
            TimeoutException,
            XmlException,
            IOException {

    	LOGGER.info("start collecting time series");
        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(metadata.getServiceUrl());

        Map<SosTimeseries, FutureTask<OperationResult>> getDataAvailabilityTasks = new HashMap<SosTimeseries, FutureTask<OperationResult>>();
        Map<String, FutureTask<OperationResult>> getFoiAccessTasks = new HashMap<String, FutureTask<OperationResult>>();

        // create tasks by iteration over procedures
        for (SosTimeseries timeserie : observingTimeseries) {
            String procedureID = timeserie.getProcedureId();
            getFoiAccessTasks.put(procedureID,
                                  new FutureTask<OperationResult>(createGetFoiAccess(metadata.getServiceUrl(),
                                                                                     metadata.getVersion(),
                                                                                     procedureID)));
            getDataAvailabilityTasks.put(timeserie,
                                         new FutureTask<OperationResult>(createGDAAccess(metadata.getServiceUrl(),
                                                                                         metadata.getVersion(),
                                                                                         timeserie)));
        }

        // create list of timeseries of GDA requests
        Collection<SosTimeseries> timeseries = executeGDATasks(getDataAvailabilityTasks, metadata);

        // iterate over tasks of getFOI and add them to metadata
        executeFoiTasks(getFoiAccessTasks, metadata);

        // iterate over timeseries and add them to station with according feature id
        for (SosTimeseries timeserie : timeseries) {
            String feature = timeserie.getFeatureId();
            Station station = metadata.getStation(feature);
            if (station != null) {
                station.addTimeseries(timeserie);
            }
            else {
                LOGGER.warn("{} not added! No station for feature '{}'.", timeserie, feature);
            }
        }

        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
    }

    private Collection<SosTimeseries> executeGDATasks(Map<SosTimeseries, FutureTask<OperationResult>> getDataAvailabilityTasks,
                                                      SOSMetadata metadata) throws InterruptedException,
            ExecutionException,
            TimeoutException,
            XmlException,
            IOException {
        int counter = getDataAvailabilityTasks.size();
        LOGGER.debug("Sending " + counter + " GetDataAvailability requests");
        Collection<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
        for (SosTimeseries timeserie : getDataAvailabilityTasks.keySet()) {
            LOGGER.debug("Sending #{} GetDataAvailability request for procedure " + timeserie.getProcedureId(),
                         counter--);
            FutureTask<OperationResult> futureTask = getDataAvailabilityTasks.get(timeserie);
            AccessorThreadPool.execute(futureTask);
            OperationResult result = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
            if (result == null) {
                LOGGER.error("Get no result for GetDataAvailability with parameter constellation: " + timeserie + "!");
            }
            XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsStream());
            timeseries.addAll(getAvailableTimeseries(result_xb, timeserie, metadata));
        }
        return timeseries;
    }

    private void executeFoiTasks(Map<String, FutureTask<OperationResult>> getFoiAccessTasks, SOSMetadata metadata) throws InterruptedException,
            ExecutionException,
            XmlException,
            IOException,
            OXFException {
        int counter;
        counter = getFoiAccessTasks.size();
        LOGGER.debug("Sending {} GetFeatureOfInterest requests", counter);
        for (String procedureID : getFoiAccessTasks.keySet()) {
            LOGGER.debug("Sending #{} GetFeatureOfInterest request for procedure '{}'", counter--, procedureID);
            FutureTask<OperationResult> futureTask = getFoiAccessTasks.get(procedureID);
            AccessorThreadPool.execute(futureTask);
            try {
                OperationResult opRes = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
                GetFeatureOfInterestParser getFoiParser = new GetFeatureOfInterestParser(opRes, metadata);
                getFoiParser.createFeatures();
            }
            catch (TimeoutException e) {
                LOGGER.error("Timeout occured.", e);
            }
        }
    }

    protected Collection<SosTimeseries> getAvailableTimeseries(XmlObject result_xb,
                                                             SosTimeseries timeserie,
                                                             SOSMetadata metadata) throws XmlException, IOException {
        ArrayList<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
        String queryExpression = "declare namespace gda='http://www.opengis.net/sosgda/1.0'; $this/gda:GetDataAvailabilityResponse/gda:dataAvailabilityMember";
        XmlObject[] response = result_xb.selectPath(queryExpression);
        for (XmlObject xmlObject : response) {
            SosTimeseries addedtimeserie = new SosTimeseries();
            String feature = getAttributeOfChildren(xmlObject, "featureOfInterest", "href");
            String phenomenon = getAttributeOfChildren(xmlObject, "observedProperty", "href");
            String procedure = getAttributeOfChildren(xmlObject, "procedure", "href");
            addedtimeserie.setFeature(new Feature(feature, metadata.getServiceUrl()));
            addedtimeserie.setPhenomenon(new Phenomenon(phenomenon, metadata.getServiceUrl()));
            addedtimeserie.setProcedure(new Procedure(procedure, metadata.getServiceUrl()));
            // create the category for every parameter constellation out of phenomenon and procedure
            String category = getLastPartOf(phenomenon) + " (" + getLastPartOf(procedure) + ")";
            addedtimeserie.setCategory(new Category(category, metadata.getServiceUrl()));
            addedtimeserie.setOffering(new Offering(timeserie.getOfferingId(), metadata.getServiceUrl()));
            addedtimeserie.setSosService(new SosService(timeserie.getServiceUrl(), metadata.getVersion()));
            addedtimeserie.getSosService().setLabel(metadata.getTitle());
            timeseries.add(addedtimeserie);
        }
        return timeseries;
    }

    protected String getAttributeOfChildren(XmlObject xmlObject, String child, String attribute) {
    	XmlObject[] children = xmlObject.selectChildren("http://www.opengis.net/sosgda/1.0", child);
        SimpleValue childObject = ((org.apache.xmlbeans.SimpleValue) children[0].selectAttribute("http://www.w3.org/1999/xlink",
                                                                                                                        attribute));
        return childObject.getStringValue();
    }

    protected String getLastPartOf(String phenomenonId) {
        return phenomenonId.substring(phenomenonId.lastIndexOf("/") + 1);
    }

    private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, String procedureID) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("procedure", procedureID);
        Operation operation = new Operation(GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
        return new OperationAccessor(getSosAdapter(), operation, container);
    }

    private Callable<OperationResult> createGDAAccess(String sosUrl, String version, SosTimeseries timeserie) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell("procedure", timeserie.getProcedureId());
        container.addParameterShell("version", version);
        Operation operation = new Operation(GET_DATA_AVAILABILITY, sosUrl, sosUrl);
        return new OperationAccessor(getSosAdapter(), operation, container);
    }

}
