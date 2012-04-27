package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bson.types.ObjectId;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

import com.google.code.morphia.annotations.Id;
import com.mongodb.gridfs.GridFSInputFile;

public class Document {
    @Id
    private ObjectId id;
    private ObjectId document;
    private int wordCount;
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

    public int getWordCount() {
	return wordCount;
    }

    public Document setWordCount(int wordCount) {
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
	    gFile = fs.createFile(file);
	    gFile.save();
	    try {
		FileInputStream fis = new FileInputStream(
			file.getAbsolutePath());
		Document doc = new Document();
		doc.setDocument((ObjectId) gFile.getId()).setTitle(
			file.getName());
		if (file.getName().endsWith(".docx")) {
		    // StringWriter wr = new StringWriter();
		    // TextUtils.extractText(dc, wr);
		    // // System.out
		    // // .println(wr.getBuffer().toString().split(" ").length);
		    WordprocessingMLPackage dc = WordprocessingMLPackage
			    .load(fis);
		    MainDocumentPart documentPart = dc.getMainDocumentPart();

		    org.docx4j.wml.Document wmlDocumentEl = documentPart
			    .getJaxbElement();
		    StringWriter str = new StringWriter();
		    TextUtils.extractText(wmlDocumentEl, str);

		    WordCount.linecount("/home/user/Desktop/t.txt");
		    System.out.println(str.getBuffer().toString()
			    .split("( |\n|\r)").length);
		    boolean inWord = false;
		    int numChars = 0;
		    int numWords = 0;
		    int numLines = 0;

		    for (int i = 0; i < str.getBuffer().toString().length(); i++) {
			final char c = str.getBuffer().toString().charAt(i);
			numChars++;
			switch (c) {
			case '\n':
			    numLines++;
			    // FALLSTHROUGH
			case '\t':
			case ' ':
			    if (inWord) {
				numWords++;
				inWord = false;
			    }
			    break;
			default:
			    inWord = true;
			}
		    }

		    System.out.println("\t" + numLines + "\t" + numWords + "\t"
			    + numChars);

		    XWPFDocument document = new XWPFDocument(fis);
		    XWPFWordExtractor extractor = new XWPFWordExtractor(
			    document);
		    return doc.setWordCount(extractor.getExtendedProperties()
			    .getUnderlyingProperties().getWords());
		} else {
		    HWPFDocument document = new HWPFDocument(fis);
		    WordExtractor extractor = new WordExtractor(document);
		    System.out.println(extractor.getText().split(" ").length);
		    return doc.setWordCount(extractor.getSummaryInformation()
			    .getWordCount());
		}
	    } catch (Exception e) {
		return null;
	    }
	} catch (IOException e1) {
	    return null;
	}
    }

    public void delete() {
	fs.remove(document);
    }
}
