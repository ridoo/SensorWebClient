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
package org.n52.client.event.data;

import static org.n52.client.event.i18n.SesStringsAccessor.i18n;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class UserDataSource extends DataSource {

    public UserDataSource() {

        DataSourceIntegerField id = new DataSourceIntegerField("parameterId", "ID");
        id.setPrimaryKey(true);
        id.setHidden(true);

        DataSourceTextField userName = new DataSourceTextField("userName", i18n.userName());
        DataSourceTextField name = new DataSourceTextField("name", i18n.name());
        DataSourceTextField password = new DataSourceTextField("password", i18n.password());
        DataSourceTextField eMail = new DataSourceTextField("eMail", i18n.emailAddress());
        DataSourceTextField handy = new DataSourceTextField("handy", i18n.handyNumber());
        DataSourceTextField role = new DataSourceTextField("role", i18n.role());

        setFields(id, userName, name, password, eMail, handy, role);

        setClientOnly(true);
    }
}