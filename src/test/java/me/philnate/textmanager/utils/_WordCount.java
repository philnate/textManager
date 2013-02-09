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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import me.philnate.textmanager.TestBase;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class _WordCount extends TestBase {

    private static ClassLoader loader = _WordCount.class.getClassLoader();

    @Test
    public void testSmallDoc() throws FileNotFoundException, IOException {
	assertEquals(8, WordCount.countFile(new ClassPathResource(
		"docs/short.doc", loader).getFile()));
    }

    @Test
    public void testSmallDocX() throws FileNotFoundException, IOException {
	assertEquals(8, WordCount.countFile(new ClassPathResource(
		"docs/short.docx", loader).getFile()));

    }

    @Test
    public void testMiddleDoc() throws FileNotFoundException, IOException {
	assertEquals(66, WordCount.countFile(new ClassPathResource(
		"docs/middle.doc", loader).getFile()));
    }

    @Test
    public void testMiddleDocX() throws FileNotFoundException, IOException {
	assertEquals(66, WordCount.countFile(new ClassPathResource(
		"docs/middle.docx", loader).getFile()));
    }

    @Test
    public void testLargeDoc() throws FileNotFoundException, IOException {
	assertEquals(567, WordCount.countFile(new ClassPathResource(
		"docs/long.doc", loader).getFile()));
    }

    @Test
    public void testLargeDocX() throws FileNotFoundException, IOException {
	assertEquals(567, WordCount.countFile(new ClassPathResource(
		"docs/long.docx", loader).getFile()));
    }

    @Test
    public void testNotSupportedFiles() {
	try {
	    WordCount.countFile(new ClassPathResource("docs/unsupported",
		    loader).getFile());
	    fail("File type isn't supported");
	} catch (Exception e) {
	    assertThat(e.getMessage(), containsString("non doc(X) file"));

	}
	try {
	    WordCount.countFile(new ClassPathResource("docs/unsupported.txt",
		    loader).getFile());
	    fail("File type isn't supported");
	} catch (Exception e) {
	    assertThat(e.getMessage(), containsString("non doc(X) file"));

	}
	try {
	    WordCount.countFile(new ClassPathResource("docs/unsupported.odp",
		    loader).getFile());
	    fail("File type isn't supported");
	} catch (Exception e) {
	    assertThat(e.getMessage(), containsString("non doc(X) file"));

	}
    }
}
