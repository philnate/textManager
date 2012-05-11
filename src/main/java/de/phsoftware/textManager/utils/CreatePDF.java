package de.phsoftware.textManager.utils;

import static de.phsoftware.textManager.utils.DB.ds;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.bson.types.ObjectId;

import de.phsoftware.textManager.entities.Customer;
import de.phsoftware.textManager.entities.Setting;

public class CreatePDF {
    private final String billNo;
    private final int month;
    private final int year;
    private final Customer customer;

    public static void main(String[] args) throws IOException,
	    InterruptedException {
	new CreatePDF(new ObjectId("4f887c5844ae787901a6d271"), "billNo", 4,
		2012);
    }

    public CreatePDF(ObjectId customer, String billNo, int month, int year)
	    throws IOException, InterruptedException {
	this.billNo = billNo;
	this.month = month;
	this.year = year;
	this.customer = Customer.load(customer);
	preparePDF();
    }

    @SuppressWarnings("deprecation")
    public void preparePDF() throws IOException, InterruptedException {
	File template = new File(Setting.findSetting("template").getValue());
	File path = template.getParentFile();
	// System.out.println(template.);
	Velocity.setProperty("file.resource.loader.path",
		path.getAbsolutePath());
	Velocity.init();
	VelocityContext ctx = new VelocityContext();
	Iterator<Setting> it = ds.find(Setting.class).iterator();
	while (it.hasNext()) {
	    Setting cur = it.next();
	    ctx.put(cur.getKey(), cur.getValue());
	}
	StringWriter writer = new StringWriter();
	Velocity.mergeTemplate(template.getName(), ctx, writer);
	File filledTemplate = new File(path, year + "" + month + billNo
		+ ".tex");
	FileUtils.writeStringToFile(filledTemplate, writer.toString());

	// TODO improve creation of pdf else that it doesn't block or that it's
	// killed after some time if nothing happened

	ProcessBuilder pdfLatex = new ProcessBuilder(Setting.findSetting(
		"pdfLatex").getValue(), "-interaction nonstopmode",
		"-output-format pdf", filledTemplate.toString());
	pdfLatex.directory(path);
	if (0 == printOutputStream(pdfLatex.start())) {
	    // display Bill in DocumentViewer
	    new ProcessBuilder(Setting.findSetting("pdfViewer").getValue(),
		    filledTemplate.toString().replaceAll("tex$", "pdf"))
		    .start().waitFor();
	} else {
	    new JOptionPane(
		    "Bei der Erstellung der Rechnung ist ein Fehler aufgetreten. Es wurde keine Rechnung erstellt.\n Bitte Schauen sie in die Logdatei für nähere Fehlerinformationen.",
		    JOptionPane.ERROR_MESSAGE).setVisible(true);
	}
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
