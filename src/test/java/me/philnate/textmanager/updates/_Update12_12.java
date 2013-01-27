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

import static me.philnate.textmanager.utils.DB.ds;
import static org.junit.Assert.assertEquals;
import me.philnate.textmanager.entities.Bill;
import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.entities.Setting;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.google.code.morphia.Morphia;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class _Update12_12 extends UpdateTestBase {

    Morphia morphia = new Morphia();

    @Test
    public void testCorrectUpdate() {
	DBObject bill = morphia.toDBObject(new Bill()
		.setCustomer(new ObjectId()).setYear(2013).setMonth(1));
	bill.put("className", Bill.class.getName());
	ds.getCollection(Bill.class).save(bill);

	DBObject billingItem = morphia.toDBObject(new BillingItem()
		.setCustomerId(new ObjectId()).setMonth(11));
	billingItem.put("className", BillingItem.class.getName());
	ds.getCollection(BillingItem.class).save(billingItem);

	DBObject setting = morphia.toDBObject(new Setting("test", "value"));
	setting.put("className", Setting.class.getName());
	ds.getCollection(Setting.class).save(setting);

	Update update = new Update12_12();
	// update procedure
	update.preCheck();
	update.upgrade();
	update.postCheck();

	// now manually verify correct state
	// verifying that classNames are correctly removed from documents
	assertEquals(
		1,
		ds.getCollection(Bill.class)
			.find(QueryBuilder.start("className").exists(false)
				.and("customerId").is(bill.get("customerId"))
				.and("year").is(bill.get("year")).and("month")
				.is(bill.get("month")).get()).count());
	assertEquals(
		1,
		ds.getCollection(BillingItem.class)
			.find(QueryBuilder.start("className").exists(false)
				.and("customerId")
				.is(billingItem.get("customerId"))
				.and("customerId")
				.is(billingItem.get("customerId")).and("month")
				.is(billingItem.get("month")).get()).count());
	assertEquals(
		1,
		ds.getCollection(Setting.class)
			.find(QueryBuilder.start("className").exists(false)
				.and("_id").is(setting.get("_id")).and("value")
				.is(setting.get("value")).get()).count());

	// verify that names are split correctly
    }

    public void testWrongPreconditions() {

    }
}