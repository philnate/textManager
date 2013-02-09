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
package me.philnate.textmanager.entities;

import static java.lang.String.format;
import static me.philnate.textmanager.utils.DB.ds;
import static me.philnate.textmanager.utils.I18N.getCaption;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private String firstName;
    private String lastName;

    private static Logger LOG = LoggerFactory.getLogger(Customer.class);

    public String getFirstName() {
	return firstName;
    }

    public Customer setFirstName(String firstName) {
	this.firstName = firstName;
	return this;
    }

    public String getLastName() {
	return lastName;
    }

    public Customer setLastName(String lastName) {
	this.lastName = lastName;
	return this;
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

    @Override
    public String toString() {
	return companyName + "|" + lastName;
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
	LOG.debug(format("Deleting customer Bills for %s", this));
	for (Bill bill : Bill.find().filter("customerId", id).asList()) {
	    bill.delete();
	}
	LOG.debug("Deleting customer itself");
	ds.delete(this, WriteConcern.SAFE);
    }
}
