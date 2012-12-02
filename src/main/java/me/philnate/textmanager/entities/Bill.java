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

import static me.philnate.textmanager.utils.DB.ds;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.query.Query;
import com.mongodb.WriteConcern;

@Entity(noClassnameStored = true)
@Indexes(@Index(value = "customerId, year, month", unique = true))
public class Bill extends Entry {

    @Id
    private ObjectId id;
    private ObjectId customerId;
    private int year;
    private int month;
    @Indexed(unique = true)
    private String billNo;

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
	return find().filter("customerId", customer).filter("year", year)
		.filter("month", month).get();
    }

    @Override
    public void delete() {
	for (BillingItem item : BillingItem.find(getCustomer(), year, month)) {
	    item.delete();
	}
	ds.delete(this, WriteConcern.SAFE);
    }
}
