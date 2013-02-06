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

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.google.common.collect.Lists;
import com.mongodb.WriteConcern;

/**
 * Entity which represents details about a single BillingItem like title, price,
 * wordCount, ...
 * 
 * @author philnate
 * 
 */
@Entity(noClassnameStored = true)
public class BillingItem extends Entry {
    @Id
    private ObjectId id;
    private String title;
    private int wordCount;
    private double centPerWord;
    private boolean fixedPrice;
    private double total;
    private int month;
    private int year;
    private ObjectId customerId;
    private List<Document> documents = Lists.newArrayList();

    private static Logger LOG = LoggerFactory.getLogger(BillingItem.class);

    public ObjectId getId() {
	return id;
    }

    public BillingItem setId(ObjectId id) {
	this.id = id;
	return this;
    }

    public String getTitle() {
	return title;
    }

    public BillingItem setTitle(String title) {
	this.title = title;
	return this;
    }

    public int getWordCount() {
	return wordCount;
    }

    public BillingItem setWordCount(int wordCount) {
	this.wordCount = wordCount;
	return this;
    }

    public double getCentPerWord() {
	return centPerWord;
    }

    public BillingItem setCentPerWord(double centPerWord) {
	this.centPerWord = centPerWord;
	return this;
    }

    public boolean isFixedPrice() {
	return fixedPrice;
    }

    public BillingItem setFixedPrice(boolean fixedPrice) {
	this.fixedPrice = fixedPrice;
	return this;
    }

    public double getTotal() {
	return total;
    }

    public BillingItem setTotal(double total) {
	this.total = total;
	return this;
    }

    public int getMonth() {
	return month;
    }

    public BillingItem setMonth(int month) {
	this.month = month;
	return this;
    }

    public int getYear() {
	return year;
    }

    public BillingItem setYear(int year) {
	this.year = year;
	return this;
    }

    public ObjectId getCustomerId() {
	return customerId;
    }

    public BillingItem setCustomerId(ObjectId customerId) {
	this.customerId = customerId;
	return this;
    }

    public List<Document> getDocuments() {
	return documents;
    }

    public BillingItem setDocuments(List<Document> documents) {
	this.documents = documents;
	wordCount = 0;
	for (Document doc : documents) {
	    wordCount += doc.getWordCount();
	}
	recalculate();
	return this;
    }

    public BillingItem addDocument(Document document) {
	if (documents.size() == 0) {
	    documents = new ArrayList<Document>();
	    title = document.getTitle();
	}
	this.documents.add(document);
	this.wordCount += document.getWordCount();
	recalculate();
	return this;
    }

    public void removeDocument(ObjectId document) {
	for (Document doc : documents) {
	    if (doc.getDocument().equals(document)) {
		doc.delete();
		documents.remove(doc);
		wordCount -= doc.getWordCount();
		recalculate();
		return;
	    }
	}
    }

    private void recalculate() {
	if (!fixedPrice) {
	    total = centPerWord * wordCount;
	}
    }

    public static Query<BillingItem> find() {
	return ds.find(BillingItem.class);
    }

    public static BillingItem find(ObjectId id) {
	return ds.get(BillingItem.class, id);
    }

    public static List<BillingItem> find(ObjectId customer, int year, int month) {
	return find().filter("month", month).filter("year", year)
		.filter("customerId", customer).asList();
    }

    @Override
    public void delete() {
	LOG.debug(format("Deleting BillingItem documents for %s", this));
	for (Document doc : documents) {
	    doc.delete();
	}
	LOG.debug("Deleting BillingItem itself");
	ds.delete(this, WriteConcern.SAFE);
    }
}
