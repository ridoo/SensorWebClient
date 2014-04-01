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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.n52.io.IoParameters;
import org.n52.io.v1.data.CategoryOutput;
import org.n52.series.api.v1.aggregate.output.CategoryOutputMerger;
import org.n52.web.v1.srv.ParameterService;

/**
 * Aggregates the result of multiple category requests by means of a CategoryOutputMerger.
 */
public class CategoryOutputAggregator extends TimeseriesServiceProvider implements ParameterService<CategoryOutput> {

    private CategoryOutputMerger merger = new CategoryOutputMerger();

    @Override
    public CategoryOutput[] getExpandedParameters(IoParameters query) {
        List<CategoryOutput> outputs = new ArrayList<CategoryOutput>();
        for (ParameterService<CategoryOutput> service : getCategoryServices()) {
            outputs.addAll(Arrays.asList(service.getExpandedParameters(query)));
        }
        return outputs.toArray(new CategoryOutput[0]);
    }

    @Override
    public CategoryOutput[] getCondensedParameters(IoParameters query) {
        List<CategoryOutput> outputs = new ArrayList<CategoryOutput>();
        for (ParameterService<CategoryOutput> service : getCategoryServices()) {
            outputs.addAll(Arrays.asList(service.getCondensedParameters(query)));
        }
        return outputs.toArray(new CategoryOutput[0]);
    }

    @Override
    public CategoryOutput[] getParameters(String[] items) {
        List<CategoryOutput> outputs = new ArrayList<CategoryOutput>();
        for (ParameterService<CategoryOutput> service : getCategoryServices()) {
            outputs.addAll(Arrays.asList(service.getParameters(items)));
        }
        return outputs.toArray(new CategoryOutput[0]);
    }

    @Override
    public CategoryOutput[] getParameters(String[] items, IoParameters query) {
        List<CategoryOutput> outputs = new ArrayList<CategoryOutput>();
        for (ParameterService<CategoryOutput> service : getCategoryServices()) {
            outputs.addAll(Arrays.asList(service.getParameters(items, query)));
        }
        return outputs.toArray(new CategoryOutput[0]);
    }

    @Override
    public CategoryOutput getParameter(String item) {
        CategoryOutput result = null;
        for (ParameterService<CategoryOutput> service : getCategoryServices()) {
            result = service.getParameter(item);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public CategoryOutput getParameter(String item, IoParameters query) {
        CategoryOutput result = null;
        for (ParameterService<CategoryOutput> service : getCategoryServices()) {
            result = service.getParameter(item, query);
            if (result != null) {
                break;
            }
        }
        return result;
    }

}
