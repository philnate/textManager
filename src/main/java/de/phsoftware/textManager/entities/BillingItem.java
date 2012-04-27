package de.phsoftware.textManager.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * Entity which represents details about a single BillingItem like title, price, wordCount, ...
 * @author philnate
 *
 */
@Entity
public class BillingItem {
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
    private List<Document> documents = null;

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

    public void setDocuments(List<Document> documents) {
	this.documents = documents;
	wordCount = 0;
	for (Document doc:documents) {
	    wordCount += doc.getWordCount();
	}
	recalculate();
    }
    
    public void addDocument(Document document) {
	if (null == this.documents) {
	    documents = new ArrayList<Document>();
	    title = document.getTitle();
	}
	this.documents.add(document);
	this.wordCount += document.getWordCount();
	recalculate();
    }
    
    public void removeDocument(ObjectId document) {
	for (Document doc:documents) {
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
}
