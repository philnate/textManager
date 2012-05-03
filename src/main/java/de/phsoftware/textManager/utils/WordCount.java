package de.phsoftware.textManager.utils;

import java.io.IOException;

/**
 * WordCounting functionality based on:
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

}