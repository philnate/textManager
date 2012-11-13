/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) ${year} philnate
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
package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;
import static de.phsoftware.textManager.utils.I18N.getCaption;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.mongodb.WriteConcern;

/**
 * Represents the Customer for which texter jobs are done, contains Address and
 * such stuff
 * 
 * @author philnate
 * 
 */
@Entity(noClassnameStored = true)
public class Customer extends Entry {
    @Id
    private ObjectId id;
    private String companyName;
    private String zip;
    private String street;
    private String streetNo;
    private String city;
    private boolean male;
    // TODO split contact name into first and lastname
    private String contactName;

    public String getFirstName() {
	return contactName.split(" ")[0];
    }

    public String getLastName() {
	String[] lastName = contactName.split(" ");
	if (1 == lastName.length) {
	    return lastName[0];
	} else {
	    return lastName[1];
	}
    }

    public ObjectId getId() {
	return id;
    }

    public String getCompanyName() {
	return companyName;
    }

    public Customer setCompanyName(String companyName) {
	this.companyName = companyName;
	return this;
    }

    public String getZip() {
	return zip;
    }

    public Customer setZip(String zip) {
	this.zip = zip;
	return this;
    }

    public String getStreet() {
	return street;
    }

    public Customer setStreet(String street) {
	this.street = street;
	return this;
    }

    public String getStreetNo() {
	return streetNo;
    }

    public Customer setStreetNo(String streetNo) {
	this.streetNo = streetNo;
	return this;
    }

    public boolean isMale() {
	return male;
    }

    public Customer setMale(boolean male) {
	this.male = male;
	return this;
    }

    public String getGender() {
	return (male) ? getCaption("gender.male") : getCaption("gender.female");
    }

    public String getContactName() {
	return contactName;
    }

    public Customer setContactName(String contactName) {
	this.contactName = contactName;
	return this;
    }

    @Override
    public String toString() {
	return companyName + "|" + contactName;
    }

    public String getCity() {
	return city;
    }

    public Customer setCity(String city) {
	this.city = city;
	return this;
    }

    public static Query<Customer> find() {
	return ds.find(Customer.class);
    }

    public static Customer find(ObjectId id) {
	return ds.get(Customer.class, id);
    }

    @Override
    public void delete() {
	for (Bill bill : Bill.find().filter("customerId", id).asList()) {
	    bill.delete();
	}
	ds.delete(this, WriteConcern.SAFE);
    }
}
