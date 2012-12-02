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
package me.philnate.textmanager.windows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;

public class _ImportWindow {

    private static String path = "importFiles/";

    /**
     * Test if Abby presented billingTable can be parsed correctly
     * 
     * @throws IOException
     */
    @Test
    public void testAbbyImport() throws IOException {
	Pattern pattern = Pattern
		.compile("[0-9]+\\s+(?<title>Texterstellung ID [0-9]+)\\s+Datum:.*(?<sum>[0-9]+,[0-9]+)$");// (?<sum>[0-9]+,[0-9]+)$
	BufferedReader reader = new BufferedReader(new InputStreamReader(this
		.getClass().getClassLoader()
		.getResourceAsStream(path + "abby.txt"), Charsets.UTF_8));
	String line;
	while ((line = reader.readLine()) != null) {
	    System.out.println(line);
	    Matcher matcher = pattern.matcher(line);
	    matcher.matches();
	    System.out.print("Read: ");
	    System.out.print(matcher.group("title") + " ");
	    System.out.println(matcher.group("sum"));
	    Assert.assertTrue(matcher.group("title").contains(
		    "Texterstellung ID"));
	    Assert.assertTrue(matcher.group("sum").contains(","));
	}
    }
}
