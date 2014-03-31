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

package org.n52.api.v1;

import java.util.Map.Entry;

import org.n52.api.v1.output.CategoryOutputMerger;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.CategoryOutput;
import org.n52.server.series.proxy.TimeseriesService;
import org.n52.server.series.proxy.TimeseriesServiceAggregator;
import org.n52.web.v1.srv.ParameterService;

/**
 * Aggregates the result of multiple category requests by means of a CategoryOutputMerger.
 */
public class CategoryServiceAggregator implements ParameterService<CategoryOutput> {

    private TimeseriesServiceAggregator aggregator = new TimeseriesServiceAggregator();
    
    private CategoryOutputMerger merger = new CategoryOutputMerger();

    @Override
    public CategoryOutput[] getExpandedParameters(IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput[] getCondensedParameters(IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput[] getParameters(String[] items) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput[] getParameters(String[] items, IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput getParameter(String item) {
        CategoryOutput result = null;
        for (Entry<String, TimeseriesService> entry : aggregator.getServices()) {
            TimeseriesService service = entry.getValue();
            result = service.getCategoriesService().getParameter(item);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public CategoryOutput getParameter(String item, IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    public TimeseriesServiceAggregator getAggregator() {
        return aggregator;
    }

    public void setAggregator(TimeseriesServiceAggregator aggregator) {
        this.aggregator = aggregator;
    }

}
