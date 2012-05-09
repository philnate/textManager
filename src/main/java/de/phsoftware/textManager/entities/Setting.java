package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.swing.JOptionPane;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

/**
 * simply key value document storing general settings about the app
 * 
 * @author user
 * 
 */
@Entity(noClassnameStored = true)
public class Setting {

    @Id
    private String key;
    private String value;

    public Setting() {

    }

    public Setting(String key, String value) {
	setKey(key);
	setValue(value);
    }

    public String getKey() {
	return key;
    }

    public Setting setKey(String key) {
	this.key = key;
	return this;
    }

    public String getValue() {
	return value;
    }

    public Setting setValue(String value) {
	this.value = value;
	return this;
    }

    public static Setting findSetting(String key) {
	Iterator<Setting> it = ds.find(Setting.class).filter("key =", key)
		.limit(1).fetch().iterator();
	if (it.hasNext()) {
	    return it.next();
	} else {
	    return new Setting(key, "");
	}
    }

    public static void main(String[] args) throws InterruptedException,
	    IOException {
	// TODO improve creation of pdf else that it doesn't block or that it's
	// killed after some time if nothing happened
	String billId = getBillId();
	final String texFile = Setting.findSetting("template").getValue()
		+ billId + ".tex";
	printOutputStream(new ProcessBuilder("cp", Setting.findSetting(
		"template").getValue()
		+ "document.tex", texFile).start());
	ProcessBuilder pdfLatex = new ProcessBuilder(Setting.findSetting(
		"pdflatex").getValue(), "-interaction nonstopmode",
		"-output-format pdf", texFile);
	pdfLatex.directory(new File(Setting.findSetting("template").getValue()));
	if (0 == printOutputStream(pdfLatex.start())) {
	    // display Bill in DocumentViewer
	    new ProcessBuilder(Setting.findSetting("pdfViewer").getValue(),
		    texFile.replaceAll("tex$", "pdf")).start().waitFor();
	} else {
	    new JOptionPane(
		    "Bei der Erstellung der Rechnung ist ein Fehler aufgetreten. Es wurde keine Rechnung erstellt.\n Bitte Schauen sie in die Logdatei für nähere Fehlerinformationen.",
		    JOptionPane.ERROR_MESSAGE).setVisible(true);
	}
    }

    private static String getBillId() {
	return "1";
    }

    private static int printOutputStream(Process proc) throws IOException,
	    InterruptedException {
	InputStream in = new BufferedInputStream(proc.getInputStream());
	int c;
	while ((c = in.read()) != -1) {
	    System.out.print((char) c);
	}
	in.close();
	int rc = proc.waitFor();
	System.out.println("Return code:" + rc);
	return rc;
    }
}
