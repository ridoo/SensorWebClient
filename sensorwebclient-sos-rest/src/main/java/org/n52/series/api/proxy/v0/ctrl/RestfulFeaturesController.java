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
package org.n52.series.api.proxy.v0.ctrl;

import static org.n52.series.api.proxy.v0.ctrl.RestfulUrls.DEFAULT_PATH;

import javax.servlet.http.HttpServletRequest;

import org.n52.series.api.proxy.v0.out.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH, produces = {"application/json"})
public class RestfulFeaturesController extends QueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_FEATURES, method = RequestMethod.GET)
    public ModelAndView getFeaturesByGET(@PathVariable("instance") String instance,
                                         @RequestParam(value = KVP_SHOW, required = false) String details,
                                         @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                         @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        QueryParameters parameters = QueryParameters.createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        Feature[] features = (Feature[]) result.getResults();

        if (offset < 0) {
            return new ModelAndView("features").addObject("features", features);
        } else {
            return pageResults(features, offset, size);
        }
    }

    private ModelAndView pageResults(Feature[] features, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("features");
        return mavPage.createPagedModelAndViewFrom(features, offset, size);
    }

    // this mapping handles identifier URLs
    @RequestMapping(value = "/{instance}/" + COLLECTION_FEATURES + "/**", method = RequestMethod.GET)
    public ModelAndView getFeatureByGET(@PathVariable(value = "instance") String instance,
                                        HttpServletRequest request) throws Exception {
        String feature = getDecodedIndividuumIdentifierFor(COLLECTION_FEATURES, request);
        return createResponseView(instance, feature);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_FEATURES + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getFeatureByGET(@PathVariable(value = "instance") String instance,
                                        @PathVariable(value = "id") String feature) throws Exception {
        return createResponseView(instance, decode(feature));
    }

    private ModelAndView createResponseView(String instance, String feature) throws Exception {
        ModelAndView mav = new ModelAndView("features");
        feature = stripKnownFileExtensionFrom(feature);
        QueryParameters parameters = new QueryParameters().setFeature(feature);
        QueryResponse< ? > result = performQuery(instance, parameters);

        if (result.getResults().length == 0) {
            throw new ResourceNotFoundException("Not found.");
        }

        mav.addObject("feature", result.getResults()[0]);
        return mav;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredFeaturesQuery(parameters);
        return doQuery(query);
    }

}
