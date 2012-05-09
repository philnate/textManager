package de.phsoftware.textManager.entities;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * Represents the Customer for which texter jobs are done, contains Address and
 * such stuff
 * 
 * @author philnate
 * 
 */
@Entity(noClassnameStored = true)
public class Customer {
    @Id
    private ObjectId id;
    private String companyName;
    private String zip;
    private String street;
    private String streetNo;
    private String city;
    private boolean male;
    private String contactName;

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
}
