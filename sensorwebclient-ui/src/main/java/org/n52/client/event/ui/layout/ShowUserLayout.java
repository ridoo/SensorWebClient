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
package org.n52.client.event.ui.layout;

import static org.n52.client.event.data.UserDataSourceRecord.EMAIL;
import static org.n52.client.event.data.UserDataSourceRecord.NAME;
import static org.n52.client.event.data.UserDataSourceRecord.PARAMETERID;
import static org.n52.client.event.data.UserDataSourceRecord.PASSWORD;
import static org.n52.client.event.data.UserDataSourceRecord.ROLE;
import static org.n52.client.event.data.UserDataSourceRecord.USERNAME;
import static org.n52.client.event.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;

import java.util.List;

import org.n52.client.bus.EventBus;
import org.n52.client.event.data.UserDataSource;
import org.n52.client.event.data.UserDataSourceRecord;
import org.n52.client.event.event.DeleteUserEvent;
import org.n52.client.event.event.GetAllUsersEvent;
import org.n52.client.event.ui.CreateNewUserWindow;
import org.n52.client.event.ui.EditUserWindow;
import org.n52.client.event.ui.FormLayout;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.session.SessionInfo;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ShowUserLayout extends FormLayout {

    private ListGrid userGrid;

    private UserDataSource dataSource;
    
    private boolean first = true;

    public ShowUserLayout() {
        super(i18n.userManagement());
        
        // init database
        this.dataSource = new UserDataSource();
        
        init();
    }

    private void init() {
        this.userGrid = new ListGrid() {
            @Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

                String fieldName = this.getFieldName(colNum);

                if (fieldName.equals("editField")) {

                    // edit button
                    IButton editButton = new IButton(i18n.edit());
                    editButton.setShowDown(false);
                    editButton.setShowRollOver(false);
                    editButton.setLayoutAlign(Alignment.CENTER);
                    editButton.setPrompt(i18n.editUserData());
                    editButton.setHeight(16);
                    editButton.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                        	String id = record.getAttributeAsString(PARAMETERID);
                            String userName = record.getAttributeAsString(USERNAME);
							String name = record.getAttributeAsString(NAME);
							String password = record.getAttributeAsString(PASSWORD);
							String eMail = record.getAttributeAsString(EMAIL);
							String role = record.getAttributeAsString(ROLE);
							EditUserWindow.init(new UserDataSourceRecord(id, userName, name, password, eMail, role));
                        }
                    });

                    return editButton;

                } else if (fieldName.equals("deleteField")) {

                    // delete button
                    IButton deleteButton = new IButton(i18n.delete());
                    deleteButton.setShowDown(false);
                    deleteButton.setShowRollOver(false);
                    deleteButton.setLayoutAlign(Alignment.CENTER);
                    deleteButton.setPrompt(i18n.deleteUserData());
                    deleteButton.setHeight(16);
                    deleteButton.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            SC.ask(i18n.reallyDeleteUser() + ": " + record.getAttribute(USERNAME) +  "?", new BooleanCallback() {
                                public void execute(Boolean value) {
                                    if (value) {
                                        String userId = record.getAttribute(PARAMETERID);
                                        final SessionInfo sessionInfo = currentSession();
                                        EventBus.getMainEventBus().fireEvent(new DeleteUserEvent(sessionInfo, userId));
                                        EventBus.getMainEventBus().fireEvent(new GetAllUsersEvent(sessionInfo));
                                    }
                                }
                            });
                        }
                    });
                    return deleteButton;
                } else {
                    return null;
                }
            }
        };

        // grid config
        this.userGrid.setShowRecordComponents(true);
        this.userGrid.setShowRecordComponentsByCell(true);
        this.userGrid.setWidth100();
        this.userGrid.setHeight100();
        this.userGrid.setShowAllRecords(false);
        this.userGrid.setShowFilterEditor(true);
        this.userGrid.setFilterOnKeypress(true);
        this.userGrid.setDataSource(this.dataSource);
        this.userGrid.setAutoFetchData(true);
        this.userGrid.setShowRollOver(false);
        this.userGrid.sort(0, SortDirection.ASCENDING);
        this.userGrid.setCanResizeFields(false);

        // grid fields
        ListGridField userNameField = new ListGridField("userName", i18n.userName());
        userNameField.setAlign(Alignment.CENTER);
        
        ListGridField nameField = new ListGridField("name", i18n.name());
        nameField.setAlign(Alignment.CENTER);

        ListGridField emailField = new ListGridField("eMail", i18n.emailAddress());
        emailField.setAlign(Alignment.CENTER);

//        ListGridField handyField = new ListGridField("handy", i18n.handyNumber());
//        handyField.setAlign(Alignment.CENTER);

        ListGridField roleField = new ListGridField("role", i18n.role());
        roleField.setWidth(90);
        roleField.setAlign(Alignment.CENTER);

        ListGridField editField = new ListGridField("editField", i18n.edit());
        editField.setWidth(110);
        editField.setAlign(Alignment.CENTER);
        editField.setCanFilter(false);

        ListGridField deleteField = new ListGridField("deleteField", i18n.delete());
        deleteField.setWidth(110);
        deleteField.setAlign(Alignment.CENTER);
        deleteField.setCanFilter(false);

        this.userGrid.setFields(userNameField, nameField, emailField, roleField, editField, deleteField);

        // createNewUser button
        IButton createUserButton = new IButton(i18n.createNewUser());
        createUserButton.setShowDown(false);
        createUserButton.setShowRollOver(false);
        createUserButton.setLayoutAlign(Alignment.LEFT);
        createUserButton.setPrompt(i18n.createNewUser());
        createUserButton.setHeight(20);
        createUserButton.setWidth(130);
        createUserButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                new CreateNewUserWindow();
                CreateNewUserWindow.init();
            }
        });

        this.form.setFields(this.headerItem);
        addMember(this.form);
        addMember(this.userGrid);
        addMember(this.spacer);
        addMember(createUserButton);
    }

    /**
     * Sets the data of the table.
     * @param list 
     */
    public void setData(List<UserDTO> list) {
        UserDTO user;
        UserDataSourceRecord userRecord;
        
        if (!this.first) {
            this.userGrid.selectAllRecords();
            this.userGrid.removeSelectedData(); 
        }

        for (int i = 0; i < list.size(); i++) {
            user = list.get(i);
            userRecord = new UserDataSourceRecord(String.valueOf(user.getId()), user.getUserName(), user.getName(), user.getPassword(), user.geteMail(), user.getRole().toString());

            this.userGrid.addData(userRecord);
        }
        
        this.first = false;
        this.userGrid.fetchData();
    }
}