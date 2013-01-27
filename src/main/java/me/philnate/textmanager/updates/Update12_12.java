/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2012- philnate
 *
 *   This file is part of textManager.
 *
 *   textManager is free software: you can redistribute it and/or modify it under the terms of the
 *   GNU General Public License as published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   textManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *   See the GNU General Public License for more details. You should have received a copy of the GNU
 *   General Public License along with textManager. If not, see <http://www.gnu.org/licenses/>.
 */
package me.philnate.textmanager.updates;

import static com.google.common.base.Preconditions.checkArgument;
import static me.philnate.textmanager.utils.DB.ds;

import java.util.List;

import javax.swing.text.Document;

import me.philnate.textmanager.entities.Bill;
import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.entities.Customer;
import me.philnate.textmanager.entities.Setting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

@UpdateScript(UpdatesVersion = "12.12")
public class Update12_12 implements Update {

    private static Logger LOG = LoggerFactory.getLogger(Update12_12.class);

    @SuppressWarnings("unchecked")
    private static final List<Class<?>> classes = Lists.newArrayList(Lists
	    .newArrayList(Bill.class, BillingItem.class, Customer.class,
		    Document.class, Setting.class));

    @Override
    public void preCheck() {
	// noop, we have no preconditions
    }

    @Override
    public void upgrade() {
	// remove potential className property
	LOG.info("Going to update database to 12.12");
	LOG.info("Removing potential className attributes");
	for (Class<?> clazz : classes) {
	    LOG.info("removing className from class " + clazz);
	    ds.getCollection(clazz).update(
		    new BasicDBObject(),
		    BasicDBObjectBuilder.start().push("$unset")
			    .add("className", true).pop().get(), false, true);
	}
	LOG.info("Removed all className attributes");
    }

    @Override
    public void postCheck() {
	for (Class<?> clazz : classes) {
	    checkArgument(
		    0 == ds.getCollection(clazz)
			    .find(BasicDBObjectBuilder.start()
				    .push("className").add("$exists", true)
				    .pop().get()).count(),
		    "Found className attribute within a record of class "
			    + clazz);
	}
    }

}
