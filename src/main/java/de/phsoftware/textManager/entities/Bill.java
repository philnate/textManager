package de.phsoftware.textManager.entities;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Indexes;

@Entity(noClassnameStored = true)
@Indexes(@Index(value = "customerid, year, month", unique = true))
public class Bill {

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
}
