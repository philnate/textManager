package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.docs;
import static de.phsoftware.textManager.utils.DB.ds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.mongodb.gridfs.GridFSInputFile;

import de.phsoftware.textManager.utils.WordCount;

public class Document {
    @Id
    private ObjectId id;
    private ObjectId document;
    private long wordCount;
    private String title;
    private ObjectId billingItemId;

    public ObjectId getId() {
	return id;
    }

    public Document setId(ObjectId id) {
	this.id = id;
	return this;
    }

    public ObjectId getDocument() {
	return document;
    }

    public Document setDocument(ObjectId document) {
	this.document = document;
	return this;
    }

    public long getWordCount() {
	return wordCount;
    }

    public Document setWordCount(long wordCount) {
	this.wordCount = wordCount;
	return this;
    }

    public ObjectId getBillingItemId() {
	return billingItemId;
    }

    public String getTitle() {
	return title;
    }

    public Document setTitle(String title) {
	this.title = title;
	return this;
    }

    @Override
    public String toString() {
	return this.title + " (" + this.wordCount + ")";
    }

    public static Document loadAndSave(File file) {
	GridFSInputFile gFile;
	try {
	    gFile = docs.createFile(file);
	    gFile.save();
	    FileInputStream fis = new FileInputStream(file.getAbsolutePath());
	    Document doc = new Document();
	    doc.setDocument((ObjectId) gFile.getId()).setTitle(file.getName());
	    if (file.getName().endsWith(".docx")) {
		XWPFDocument document = new XWPFDocument(fis);
		XWPFWordExtractor extractor = new XWPFWordExtractor(document);
		return doc
			.setWordCount(WordCount.linecount(extractor.getText()));
	    } else if (file.getName().endsWith(".doc")) {
		HWPFDocument document = new HWPFDocument(fis);
		WordExtractor extractor = new WordExtractor(document);
		return doc
			.setWordCount(WordCount.linecount(extractor.getText()));
	    } else {
		throw new IllegalArgumentException(
			"Can't handle non doc(X) files");
	    }
	} catch (IOException e1) {
	    return null;
	}
    }

    public void delete() {
	docs.remove(document);
    }

    public static Query<Document> find() {
	return ds.find(Document.class);
    }

    public static Document find(ObjectId id) {
	return ds.get(Document.class, id);
    }
}
