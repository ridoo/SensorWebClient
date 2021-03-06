/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.client;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ctrl.PropertiesManager.getPropertiesManager;
import static org.n52.client.ctrl.RequestManager.hasUnfinishedRequests;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.client.ui.Toaster.getToasterInstance;
import static org.n52.client.util.ClientUtils.getDecodedParameters;
import static org.n52.ext.link.sos.PermalinkParameter.BEGIN;
import static org.n52.ext.link.sos.PermalinkParameter.END;
import static org.n52.ext.link.sos.PermalinkParameter.FEATURES;
import static org.n52.ext.link.sos.PermalinkParameter.OFFERINGS;
import static org.n52.ext.link.sos.PermalinkParameter.PHENOMENONS;
import static org.n52.ext.link.sos.PermalinkParameter.PROCEDURES;
import static org.n52.ext.link.sos.PermalinkParameter.SERVICES;
import static org.n52.ext.link.sos.PermalinkParameter.VERSIONS;
import static org.n52.shared.Constants.DEFAULT_OVERVIEW_INTERVAL;

import java.util.List;
import java.util.Map;

import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.n52.client.bus.EventBus;
import org.n52.client.bus.EventCallback;
import org.n52.client.ctrl.PermalinkController;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ctrl.TimeManager;
import org.n52.client.ses.ctrl.SesController;
import org.n52.client.sos.ctrl.SOSController;
import org.n52.client.sos.ctrl.SosDataManager;
import org.n52.client.sos.event.DatesChangedEvent;
import org.n52.client.sos.event.InitEvent;
import org.n52.client.sos.event.data.GetFeatureEvent;
import org.n52.client.sos.event.data.GetOfferingEvent;
import org.n52.client.sos.event.data.GetPhenomenonsEvent;
import org.n52.client.sos.event.data.GetProcedureEvent;
import org.n52.client.sos.event.data.GetStationForTimeseriesEvent;
import org.n52.client.sos.event.data.GetStationsWithinBBoxEvent;
import org.n52.client.sos.event.data.NewSOSMetadataEvent;
import org.n52.client.sos.event.data.OverviewIntervalChangedEvent;
import org.n52.client.sos.event.data.OverviewIntervalChangedEvent.IntervalType;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.StoreSOSMetadataEvent;
import org.n52.client.sos.ui.StationSelector;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.View;
import org.n52.client.util.ClientUtils;
import org.n52.ext.link.sos.PermalinkParameter;
import org.n52.ext.link.sos.TimeRange;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.n52.shared.serializable.pojos.sos.SosService;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public final class Application implements EntryPoint {

    private static boolean HAS_STARTED = false;

    public static boolean isHasStarted() {
        return HAS_STARTED;
    }

    public static void setHasStarted(boolean hasStarted) {
        HAS_STARTED = hasStarted;
    }

    @Override
    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable e) {
                GWT.log("Uncaught Exception", e);
            }
        });
        
        // TODO refactor startup to be more explicit
        getPropertiesManager(); // creates singleton
    }

    public static void continueStartup() {

        // init handlers before throwing events
        SosDataManager.getDataManager();
        new SOSController();
        if (ClientUtils.isSesEnabled()) {
            new SesController();
        }
        View.getView();
        Element element = Document.get().getElementById("loadingWrapper");
        while (element.hasChildNodes()) {
            element.removeChild(element.getFirstChild());
        }

        Application.finishStartup();
    }

    public static void finishStartup() {
        try {
            String currentUrl = URL.decode(Window.Location.getHref());
            if (hasQueryString(currentUrl) && !isGwtHostedModeParameterOnly()) {
                String[] services = getDecodedParameters(SERVICES);
                String[] versions = getDecodedParameters(VERSIONS);
                String[] features = getDecodedParameters(FEATURES);
                String[] offerings = getDecodedParameters(OFFERINGS);
                String[] procedures = getDecodedParameters(PROCEDURES);
                String[] phenomenons = getDecodedParameters(PHENOMENONS);
                String[] options = getDecodedParameters("options");
                TimeRange timeRange = createTimeRange();

                String locale = Window.Location.getParameter("locale");
                if (isLocaleOnly(services,versions,offerings,features,procedures,phenomenons,locale)) {
                	return;
                }

                if ( !isAllParametersAvailable(services, versions, offerings, features, procedures, phenomenons)) {
                    getToasterInstance().addErrorMessage(i18n.errorUrlParsing());
                    return;
                }

                if (timeRange.isSetStartAndEnd()) {
                    fireNewTimeRangeEvent(timeRange);
                }
                
                PermalinkController permalinkController = new PermalinkController();
                for (int i = 0; i < services.length; i++) {
                    final String service = services[i];
                    final String version = versions[i];
                    final String offering = offerings[i];
                    final String procedure = procedures[i];
                    final String phenomenon = phenomenons[i];
                    final String feature = features[i];
                    
                    SosTimeseries sosTimeseries = new SosTimeseries();
                    sosTimeseries.setFeature(new Feature(feature, service));
                    sosTimeseries.setOffering(new Offering(offering, service));
                    sosTimeseries.setPhenomenon(new Phenomenon(phenomenon,service));
                    sosTimeseries.setSosService(new SosService(service, version));
                    sosTimeseries.setProcedure(new Procedure(procedure, service));
                    GWT.log("Timeseries to load: " + sosTimeseries);
                    
                    TimeseriesRenderingOptions tsOptions = null;
                    if (options != null && options.length > i) {
                        tsOptions = createRenderingOptions(options[i]);
                        GWT.log("with options: " + options[i]);
                        permalinkController.addTimeseries(sosTimeseries, tsOptions);
                    } else {
                        permalinkController.addTimeseries(sosTimeseries);
                    }
                    
                    SOSMetadataBuilder builder = new SOSMetadataBuilder()
                            .addServiceURL(service)
                            .addServiceVersion(version);
                    SOSMetadata sosMetadata = new SOSMetadata(builder);
                    getMainEventBus().fireEvent(new StoreSOSMetadataEvent(sosMetadata));
                    getMainEventBus().fireEvent(new GetPhenomenonsEvent.Builder(service).build());
                    getMainEventBus().fireEvent(new GetFeatureEvent(service, feature));
                    getMainEventBus().fireEvent(new GetOfferingEvent(service, offering));
                    getMainEventBus().fireEvent(new GetProcedureEvent(service, procedure));
                    getMainEventBus().fireEvent(new GetStationForTimeseriesEvent(sosTimeseries));
                    getMainEventBus().fireEvent(new GetStationsWithinBBoxEvent(service, null));
                    getMainEventBus().fireEvent(new NewSOSMetadataEvent());
                }
            }
            else {
                showStationSelectorWhenConfigured();
            }
        }
        catch (Exception e) {
            if ( !GWT.isProdMode()) {
                GWT.log("Error evaluating permalink!", e);
            }
            showStationSelectorWhenConfigured();
            getToasterInstance().addErrorMessage(i18n.errorUrlParsing());
        }
        finally {
            finalEvents();
        }
    }
    
    private static TimeseriesRenderingOptions createRenderingOptions(String options) {
        if (options == null) {
            return new TimeseriesRenderingOptions();
        }
        TimeseriesRenderingOptions tsOptions = new TimeseriesRenderingOptions();
        JSONObject tsRenderingOptions = new JSONObject(parseUntrustedJson(options));
        if (tsRenderingOptions.containsKey("color")) {
            JSONString color = tsRenderingOptions.get("color").isString();
            tsOptions.setColor(color.stringValue());
        }
        if (tsRenderingOptions.containsKey("lineWidth")) {
            JSONNumber lineWidth = tsRenderingOptions.get("lineWidth").isNumber();
            tsOptions.setLineWidth((int)lineWidth.doubleValue());
        }
        return tsOptions;
    }

    private native static JavaScriptObject parseUntrustedJson(String jsonString) /*-{ 
        return $wnd.JSON.parse(jsonString);    
    }-*/; 

    private static boolean isGwtHostedModeParameterOnly() {
        Map<String, List<String>> parameters = Window.Location.getParameterMap();
        boolean hasGwtCodesrvParameter = parameters.containsKey("gwt.codesvr");
        return !GWT.isProdMode() && parameters.size() == 1 && hasGwtCodesrvParameter;
    }

    static TimeRange createTimeRange() {
        String begin = getDecodedParameter(BEGIN);
        String end = getDecodedParameter(END);
        TimeRange timeRange = TimeRange.createTimeRange(begin, end);
        return timeRange;
    }

    private static String getDecodedParameter(PermalinkParameter parameter) {
        return Window.Location.getParameter(parameter.nameLowerCase());
    }

    private static void fireNewTimeRangeEvent(TimeRange timeRange) {
        try {
            DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.ISO_8601);
            long begin = formatter.parseStrict(timeRange.getStart()).getTime();
            long end = formatter.parseStrict(timeRange.getEnd()).getTime();
            Toaster.getToasterInstance().addMessage("Begin: " + timeRange.getStart());
            Toaster.getToasterInstance().addMessage("End: " + timeRange.getEnd());
            EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(begin, end, true));
        }
        catch (Exception e) {
            if ( !GWT.isProdMode()) {
                GWT.log("Unparsable TimeRange: " + timeRange, e);
            }
        }
    }

    private static void showStationSelectorWhenConfigured() {
        PropertiesManager properties = getPropertiesManager();
        boolean showAtStartup = properties.getParameterAsBoolean("showStationSelectorAtStartup");
        if (showAtStartup) {
            StationSelector.getInst().show();
        }
    }

    private static boolean hasQueryString(String currentUrl) {
        return currentUrl.indexOf("?") > -1;
    }

    private static boolean isAllParametersAvailable(String[] serviceUrls, String[] versions, String[] offerings,
                                                    String[] fois, String[] procedures, String[] phenomenons) {
        boolean serviceUrlAvailable = serviceUrls.length != 0;
        boolean versionAvailalbe = versions.length != 0;
        boolean offeringAvailable = offerings.length != 0;
        boolean featuresAvailable = fois.length != 0;
        boolean proceduresAvailable = procedures.length != 0;
        boolean phenomenonAvailable = phenomenons.length != 0;
        return serviceUrlAvailable && versionAvailalbe && offeringAvailable && featuresAvailable && proceduresAvailable
                && phenomenonAvailable;
    }
    
    

    private static boolean isLocaleOnly(String[] services, String[] versions,
			String[] offerings, String[] features, String[] procedures,
			String[] phenomenons, String locale) {
    	boolean serviceUrlsEmpty = services.length == 0;
        boolean versionEmpty = versions.length == 0;
        boolean offeringEmpty = offerings.length == 0;
        boolean featuresEmpty = features.length == 0;
        boolean proceduresEmpty = procedures.length == 0;
        boolean phenomenonEmpty = phenomenons.length == 0;
        boolean localeAvailable = locale != null;
        return serviceUrlsEmpty && versionEmpty && offeringEmpty && featuresEmpty && proceduresEmpty && phenomenonEmpty && localeAvailable;
	}

	private static void finalEvents() {

        // check for time intervals bigger than the default overview interval (in days)
        PropertiesManager propertiesMgr = getPropertiesManager();
        int days = propertiesMgr.getParamaterAsInt(DEFAULT_OVERVIEW_INTERVAL, 5);

        TimeManager timeMgr = TimeManager.getInst();
        long timeInterval = timeMgr.daysToMillis(days);
        long currentInterval = timeMgr.getEnd() - timeMgr.getBegin();

        if (timeInterval <= currentInterval) {
            timeInterval += timeMgr.getOverviewOffset(timeMgr.getBegin(), timeMgr.getEnd());
        }

        getMainEventBus().fireEvent(new OverviewIntervalChangedEvent(timeInterval, IntervalType.DAY));
        getMainEventBus().fireEvent(new InitEvent(), new EventCallback() {

            public void onEventFired() {
                EventBus.getOverviewChartEventBus().fireEvent(StateChangeEvent.createMove());
                final Timer t = new Timer() {

                    @Override
                    public void run() {
                        if (hasUnfinishedRequests()) {
                            this.schedule(200);
                        }
                        else {
                            HAS_STARTED = false;
                            getMainEventBus().fireEvent(new RequestDataEvent());
                        }
                    }
                };
                t.schedule(200);
            }
        });
    }

}
