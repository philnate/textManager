package de.phsoftware.textManager.utils;

import static de.phsoftware.textManager.utils.DB.ds;
import static de.phsoftware.textManager.utils.DB.pdf;
import static de.phsoftware.textManager.utils.DB.tex;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormatSymbols;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.MathTool;
import org.bson.types.ObjectId;

import com.mongodb.QueryBuilder;
import com.mongodb.gridfs.GridFSFile;

import de.phsoftware.textManager.entities.Bill;
import de.phsoftware.textManager.entities.BillingItem;
import de.phsoftware.textManager.entities.Customer;
import de.phsoftware.textManager.entities.Setting;

public class CreatePDF {
    private final Bill bill;
    private final int month;
    private final int year;
    private final Customer customer;

    /**
     * For testing purpose only
     * 
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException,
	    InterruptedException {
	new CreatePDF(new Bill().setBillNo("unique").setMonth(4).setYear(2012)
		.setCustomer(new ObjectId("4fad540444ae015226ff21e3")));
    }

    public CreatePDF(Bill bill) throws IOException, InterruptedException {
	this.bill = bill;
	this.month = bill.getMonth();
	this.year = bill.getYear();
	this.customer = Customer.load(bill.getCustomer());
	preparePDF();
    }

    @SuppressWarnings("deprecation")
    public void preparePDF() throws IOException, InterruptedException {
	File path = new File(System.getProperty("user.dir"), "template");
	File template = new File(path, Setting.findSetting("template")
		.getValue());

	Velocity.setProperty("file.resource.loader.path",
		path.getAbsolutePath());
	Velocity.init();
	VelocityContext ctx = new VelocityContext();

	// User data/Settings
	Iterator<Setting> it = ds.find(Setting.class).iterator();
	while (it.hasNext()) {
	    Setting cur = it.next();
	    ctx.put(cur.getKey(), cur.getValue());
	}

	// TODO update schema to have separate first and lastname
	// Customer data
	ctx.put("customer", customer);

	// General data
	ctx.put("month", new DateFormatSymbols().getMonths()[month]);
	ctx.put("math", new MathTool());
	// Billing data
	ctx.put("allItems", ds.find(BillingItem.class).filter("month", month)
		.filter("year", year).filter("customerId", customer.getId())
		.asList());
	ctx.put("billNo", bill.getBillNo());

	StringWriter writer = new StringWriter();
	Velocity.mergeTemplate(template.getName(), ctx, writer);
	File filledTemplate = new File(path, bill.getBillNo() + ".tex");
	FileUtils.writeStringToFile(filledTemplate, writer.toString(),
		"ISO-8859-1");

	// TODO improve creation of pdf else that it doesn't block or that it's
	// killed after some time if nothing happened

	ProcessBuilder pdfLatex = new ProcessBuilder(Setting.findSetting(
		"pdfLatex").getValue(), "-interaction nonstopmode",
		"-output-format pdf", filledTemplate.toString());

	// Saving template file (just in case it may be needed later
	GridFSFile texFile = tex.createFile(filledTemplate);
	texFile.put("month", month);
	texFile.put("year", year);
	texFile.put("customerId", customer.getId());
	texFile.save();

	pdfLatex.directory(path);
	String pdfPath = filledTemplate.toString().replaceAll("tex$", "pdf");
	if (0 == printOutputStream(pdfLatex.start())) {
	    // display Bill in DocumentViewer
	    new ProcessBuilder(Setting.findSetting("pdfViewer").getValue(),
		    pdfPath).start().waitFor();
	    System.out.println(pdfPath);
	    GridFSFile pdfFile = pdf.createFile(new File(pdfPath));
	    pdfFile.put("month", month);
	    pdfFile.put("year", year);
	    pdfFile.put("customerId", customer.getId());
	    pdf.remove(QueryBuilder.start("month").is(month).and("year")
		    .is(year).and("customerId").is(customer.getId()).get());
	    pdfFile.save();
	    File[] files = path.listFiles((FileFilter) new WildcardFileFilter(
		    bill.getBillNo() + ".*"));
	    for (File file : files) {
		FileUtils.forceDelete(file);
	    }
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
