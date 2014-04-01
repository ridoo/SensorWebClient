/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

package org.n52.series.api.v1.aggregate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.ServiceOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.series.api.v1.assemble.TimeseriesService;
import org.n52.series.api.v1.assemble.TimeseriesServiceAssemble;
import org.n52.web.v1.srv.CountingMetadataService;
import org.n52.web.v1.srv.ParameterService;
import org.n52.web.v1.srv.SearchService;
import org.n52.web.v1.srv.TimeseriesDataService;

/**
 * Provides access to SPI implementation services of an assembly of {@link TimeseriesService}s.
 */
public abstract class TimeseriesServiceProvider {

    private TimeseriesServiceAssemble assemble = new TimeseriesServiceAssemble();

    public TimeseriesServiceAssemble getAssemble() {
        return assemble;
    }

    public void setAssemble(TimeseriesServiceAssemble assemble) {
        this.assemble = assemble;
    }
    
    public Set<ParameterService<CategoryOutput>> getCategoryServices() {
        Set<ParameterService<CategoryOutput>> services = new HashSet<ParameterService<CategoryOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getCategoriesService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<FeatureOutput>> getFeatureServices() {
        Set<ParameterService<FeatureOutput>> services = new HashSet<ParameterService<FeatureOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getFeaturesService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<CountingMetadataService> getCountingMetadataServices() {
        Set<CountingMetadataService> services = new HashSet<CountingMetadataService>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getCountingMetadataService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<OfferingOutput>> getOfferingServices() {
        Set<ParameterService<OfferingOutput>> services = new HashSet<ParameterService<OfferingOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getOfferingsService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<PhenomenonOutput>> getPhenomenonServices() {
        Set<ParameterService<PhenomenonOutput>> services = new HashSet<ParameterService<PhenomenonOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getPhenomenaService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<ProcedureOutput>> getProcedureService() {
        Set<ParameterService<ProcedureOutput>> services = new HashSet<ParameterService<ProcedureOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getProceduresService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<SearchService> getSearchServices() {
        Set<SearchService> services = new HashSet<SearchService>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getSearchService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<ServiceOutput>> getServiceServices() {
        Set<ParameterService<ServiceOutput>> services = new HashSet<ParameterService<ServiceOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getServicesService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<StationOutput>> getStationServices() {
        Set<ParameterService<StationOutput>> services = new HashSet<ParameterService<StationOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getStationsService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<TimeseriesDataService> getTimeseriesDataServices() {
        Set<TimeseriesDataService> services = new HashSet<TimeseriesDataService>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getTimeseriesDataService());
        }
        return Collections.unmodifiableSet(services);
    }

    public Set<ParameterService<TimeseriesMetadataOutput>> getTimeseriesServices() {
        Set<ParameterService<TimeseriesMetadataOutput>> services = new HashSet<ParameterService<TimeseriesMetadataOutput>>();
        for (Entry<String, TimeseriesService> service : assemble.getServices()) {
            services.add(service.getValue().getTimeseriesService());
        }
        return Collections.unmodifiableSet(services);
    }

}
