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

import org.n52.io.IoParameters;
import org.n52.io.format.TvpDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.web.v1.srv.ParameterService;
import org.n52.web.v1.srv.TimeseriesDataService;

public class TimeseriesOutputAggregator extends TimeseriesServiceProvider implements TimeseriesDataService, ParameterService<TimeseriesMetadataOutput> {

    @Override
    public TimeseriesMetadataOutput[] getExpandedParameters(IoParameters query) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput[] getCondensedParameters(IoParameters query) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput[] getParameters(String[] items) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput[] getParameters(String[] items, IoParameters query) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput getParameter(String item) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput getParameter(String item, IoParameters query) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TvpDataCollection getTimeseriesData(UndesignedParameterSet parameters) {
        // TODO Auto-generated method stub
        return null;
        
    }

}
