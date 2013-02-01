package me.philnate.textmanager.utils;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class _WordCount {

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
