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
