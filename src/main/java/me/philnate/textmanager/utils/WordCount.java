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
package me.philnate.textmanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * WordCounting (lineCount and wordCount methods) functionality based on:
 * http://www.roseindia.net/java/beginners/java-word-count.shtml
 * 
 * @author philnate
 * 
 */
public class WordCount {

    @SuppressWarnings("unused")
    public static long linecount(String in) throws IOException {
	long numChar = 0;
	long numLine = 0;
	long numWords = 0;
	String[] lines = in.split("\\r?\\n");

	for (String line : lines) {
	    if (line != null) {
		numChar += line.length();
		numWords += wordcount(line);
		numLine++;
	    }
	}
	return numWords;
    }

    public static long wordcount(String line) {
	long numWords = 0;
	int index = 0;
	boolean prevWhiteSpace = true;
	while (index < line.length()) {
	    char c = line.charAt(index++);
	    boolean currWhiteSpace = Character.isWhitespace(c);
	    if (prevWhiteSpace && !currWhiteSpace) {
		numWords++;
	    }
	    prevWhiteSpace = currWhiteSpace;
	}
	return numWords;
    }

    /**
     * opens the given file, if it's a .doc or .docx file and returns the number
     * of words within the document
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static long countFile(File file) throws FileNotFoundException,
	    IOException {
	try (FileInputStream fis = new FileInputStream(file.getAbsolutePath())) {
	    if (file.getName().endsWith(".docx")) {
		XWPFDocument document = new XWPFDocument(fis);
		XWPFWordExtractor extractor = new XWPFWordExtractor(document);
		return linecount(extractor.getText());
	    } else if (file.getName().endsWith(".doc")) {
		HWPFDocument document = new HWPFDocument(fis);
		WordExtractor extractor = new WordExtractor(document);
		return WordCount.linecount(extractor.getText());
	    } else {
		throw new IllegalArgumentException(
			"Can't handle non doc(X) files");
	    }
	}
    }

}