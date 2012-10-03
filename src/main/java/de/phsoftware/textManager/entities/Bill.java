package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

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
