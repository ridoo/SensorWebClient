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
package org.n52.client.series.event.data;

import java.util.Arrays;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.series.event.data.handler.GetPhenomenonsEventHandler;

public class GetPhenomenonsEvent extends FilteredDispatchGwtEvent<GetPhenomenonsEventHandler> {

    public static Type<GetPhenomenonsEventHandler> TYPE = new Type<GetPhenomenonsEventHandler>();

    private String sosURL;

    public static class Builder {
		private String serviceURL; // required
		private GetPhenomenonsEventHandler[] blockedHandlers = new GetPhenomenonsEventHandler[0];
    	
    	public Builder(String sosURL) {
    		this.serviceURL = sosURL;
    	}

    	public Builder addBlockedHandlers(GetPhenomenonsEventHandler... handlers) {
    		this.blockedHandlers = handlers;
    		return this;
    	}
    	public GetPhenomenonsEvent build() {
    		return new GetPhenomenonsEvent(this);
    	}
    	
    	String getServiceURL() {
    		return this.serviceURL;
    	}
    	
    	GetPhenomenonsEventHandler[] getBlockedHandlers() {
    		return blockedHandlers;
    	}
    	
    }

    private GetPhenomenonsEvent(Builder builder) {
		this.sosURL = builder.getServiceURL();
		GetPhenomenonsEventHandler[] handlers = builder.getBlockedHandlers();
		getBlockedHandlers().addAll(Arrays.asList(handlers));
	}

    public String getSosURL() {
        return this.sosURL;
    }

    @Override
    protected void onDispatch(GetPhenomenonsEventHandler handler) {
        handler.onGetPhenomena(this);
    }

    @Override
    public Type<GetPhenomenonsEventHandler> getAssociatedType() {
        return TYPE;
    }

}