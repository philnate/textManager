package de.phsoftware.textManager.entities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class WordCount {
    private static void linecount(String fName, BufferedReader in)
	    throws IOException {
	long numChar = 0;
	long numLine = 0;
	long numWords = 0;
	String line;
	do {
	    line = in.readLine();
	    if (line != null) {
		numChar += line.length();
		numWords += wordcount(line);
		numLine++;
	    }
	} while (line != null);
	System.out.println("File Name: " + fName);
	System.out.println("Number of characters: " + numChar);
	System.out.println("Number of words: " + numWords);
	System.out.println("Number of Lines: " + numLine);
    }

    public static void linecount(String fileName) {
	BufferedReader in = null;
	try {
	    FileReader fileReader = new FileReader(fileName);
	    in = new BufferedReader(fileReader);
	    linecount(fileName, in);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static long wordcount(String line) {
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