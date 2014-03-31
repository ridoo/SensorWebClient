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
package org.n52.server.series.proxy;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.web.v1.srv.CountingMetadataService;
import org.n52.web.v1.srv.ParameterService;
import org.n52.web.v1.srv.SearchService;
import org.n52.web.v1.srv.ServiceParameterService;
import org.n52.web.v1.srv.TimeseriesDataService;

public interface TimeseriesService {

    public ParameterService<PhenomenonOutput> getPhenomenaService();

    public ParameterService<ProcedureOutput> getProceduresService();
    
    public ParameterService<CategoryOutput> getCategoriesService();

    public ParameterService<OfferingOutput> getOfferingsService();
    
    public ParameterService<FeatureOutput> getFeaturesService();
    
    public ParameterService<StationOutput> getStationsService();
    
    public ParameterService<TimeseriesMetadataOutput> getTimeseriesService();
    
    public TimeseriesDataService getTimeseriesDataService();

    public CountingMetadataService getCountingMetadataService();

    public ServiceParameterService getServicesService();

    public SearchService getSearchService();

}
