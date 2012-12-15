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
package me.philnate.textmanager.entities;

import static java.lang.String.format;
import static me.philnate.textmanager.utils.DB.ds;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.query.Query;
import com.mongodb.WriteConcern;

@Entity(noClassnameStored = true)
@Indexes({ @Index(value = "customerId, year, month", unique = true),
// #41 Index needs to be sparse else it's not possible to have multiple Bills
// stored without a BillNo, which is actually totally legal (can't be created
// anyway, user may not be sure of the order at this time)
	@Index(value = "billNo", unique = true, sparse = true) })
public class Bill extends Entry {

    @Id
    private ObjectId id;
    private ObjectId customerId;
    private int year;
    private int month;
    private String billNo;

    private static Logger LOG = LoggerFactory.getLogger(Bill.class);

    public ObjectId getId() {
	return id;
    }

    public Bill setId(ObjectId id) {
	this.id = id;
	return this;
    }

    public ObjectId getCustomer() {
	return customerId;
    }

    public Bill setCustomer(ObjectId customer) {
	this.customerId = customer;
	return this;
    }

    public int getYear() {
	return year;
    }

    public Bill setYear(int year) {
	this.year = year;
	return this;
    }

    public int getMonth() {
	return month;
    }

    public Bill setMonth(int month) {
	this.month = month;
	return this;
    }

    public String getBillNo() {
	return billNo;
    }

    public Bill setBillNo(String billNo) {
	this.billNo = billNo;
	return this;
    }

    public static Query<Bill> find() {
	return ds.find(Bill.class);
    }

    public static Bill find(ObjectId customer, int year, int month) {
	LOG.debug(format(
		"Lookup of Bill with customer '%s', year '%d' and month '%d'",
		customer, year, month));
	Bill res = find().filter("customerId", customer).filter("year", year)
		.filter("month", month).get();
	if (res != null) {
	    LOG.debug(format("Found Bill %s", res));
	} else {
	    LOG.debug("Found no Bill");
	}
	return res;
    }

    @Override
    public void delete() {
	LOG.debug(format("Deleting Bill %s, items", this));
	for (BillingItem item : BillingItem.find(getCustomer(), year, month)) {
	    item.delete();
	}
	LOG.debug("Deleted all BillingItems, deleting Bill itself");
	ds.delete(this, WriteConcern.SAFE);
    }
}
