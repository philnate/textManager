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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.philnate.textmanager.TestBase;

import org.junit.Test;

import com.google.common.base.Charsets;

public class _ImportWindow extends TestBase {

    private static String path = "importFiles/";

    /**
     * Test if Abby presented billingTable can be parsed correctly
     * 
     * @throws IOException
     */
    @Test
    public void testAbbyImport() throws IOException {
	Pattern pattern = Pattern
		.compile("[0-9]+\\s+(?<title>Texterstellung ID [0-9]+)\\s+Datum:.*(?<sum>[0-9]+,[0-9]+)\\s+\\k<sum>$");
	BufferedReader reader = new BufferedReader(new InputStreamReader(this
		.getClass().getClassLoader()
		.getResourceAsStream(path + "abby.txt"), Charsets.UTF_8));

	assertAbby(reader, pattern, "Texterstellung ID 2359311", "7,50");
	// initial regex ignored leading number
	assertAbby(reader, pattern, "Texterstellung ID 2358181", "120,00");
    }

    private void assertAbby(BufferedReader reader, Pattern pattern,
	    String title, String sum) throws IOException {
	String line = reader.readLine();
	System.out.println(line);
	Matcher matcher = pattern.matcher(line);
	matcher.matches();
	System.out.print("Read: ");
	System.out.print(matcher.group("title") + " ");
	System.out.println(matcher.group("sum"));
	assertTrue(matcher.group("title").equals(title));
	assertTrue(matcher.group("sum").equals(sum));
    }
}
