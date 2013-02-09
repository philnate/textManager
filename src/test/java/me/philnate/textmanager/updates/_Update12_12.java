/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2013 philnate
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

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static me.philnate.textmanager.utils.DB.ds;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import me.philnate.textmanager.TestBase;
import me.philnate.textmanager.entities.Bill;
import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.entities.Customer;
import me.philnate.textmanager.entities.Setting;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Test;

import com.google.code.morphia.Morphia;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class _Update12_12 extends TestBase {

    Morphia morphia = new Morphia();

    @After
    public void tearDown() {
	ds.getCollection(BillingItem.class).drop();
	ds.getCollection(Customer.class).drop();
    }

    @Test
    public void testCorrectClassName() {
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

    @Test
    public void testUpdateFailedClassName() {
	DBObject billingItem = morphia.toDBObject(new BillingItem()
		.setCustomerId(new ObjectId()).setMonth(11));
	billingItem.put("className", BillingItem.class.getName());
	ds.getCollection(BillingItem.class).save(billingItem);
	Update update = new Update12_12();
	// update procedure
	update.preCheck();
	// update.upgrade(); We want no real update, just wanna check that
	// postCheck is doing right
	try {
	    update.postCheck();
	    fail("postCheck should discover that something went wrong");
	} catch (IllegalArgumentException e) {
	    assertThat(
		    e.getMessage(),
		    containsString("Found className attribute within a record of class"));
	}
    }

    @Test
    public void testNameFormatting() {
	DBCollection c = ds.getCollection(Customer.class);
	DBObject customer = morphia.toDBObject(new Customer());
	customer.put("contactName", "First Lastname");
	c.save(customer);

	customer = morphia.toDBObject(new Customer());
	customer.put("contactName", "Lastname");
	c.save(customer);

	customer = morphia.toDBObject(new Customer());
	customer.put("contactName", "First Middle Lastname");
	c.save(customer);

	Update update = new Update12_12();
	// update procedure
	update.preCheck();
	update.upgrade();
	// now verify that everything is as expected
	List<Customer> cust = ds.createQuery(Customer.class).asList();
	assertEquals(3, cust.size());
	assertEquals(
		1,
		filter(
			having(on(Customer.class).getFirstName(), is("First"))
				.and(having(on(Customer.class).getLastName(),
					is("Lastname"))), cust).size());
	assertEquals(
		1,
		filter(
			having(on(Customer.class).getFirstName(), is("")).and(
				having(on(Customer.class).getLastName(),
					is("Lastname"))), cust).size());
	assertEquals(
		1,
		filter(
			having(on(Customer.class).getFirstName(), is("First"))
				.and(having(on(Customer.class).getLastName(),
					is("Middle Lastname"))), cust).size());
    }

    @Test
    public void testUpdateFailedNameFormatting() {
	DBCollection c = ds.getCollection(Customer.class);
	DBObject customer = morphia.toDBObject(new Customer());
	customer.put("contactName", "First Lastname");
	c.save(customer);

	Update update = new Update12_12();
	try {
	    update.postCheck();
	    fail("Should detect error in postCheck. ContactName remained");
	} catch (Exception e) {
	    assertThat(e.getMessage(),
		    containsString("Found customer object with contactName"));
	}

	customer.removeField("contactName");
	c.save(customer);
	try {
	    update.postCheck();
	    fail("Should detect error in postCheck. No lastName found.");
	} catch (Exception e) {
	    assertThat(e.getMessage(), containsString("Found customer without"));
	}
    }
}