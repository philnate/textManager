/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) ${year} philnate
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
package de.phsoftware.textManager.utils;

import static de.phsoftware.textManager.utils.DB.ds;
import static de.phsoftware.textManager.utils.DB.pdf;
import static de.phsoftware.textManager.utils.DB.tex;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.tools.generic.MathTool;
import org.bson.types.ObjectId;

import com.google.common.base.Throwables;
import com.mongodb.QueryBuilder;
import com.mongodb.gridfs.GridFSFile;

import de.phsoftware.textManager.entities.Bill;
import de.phsoftware.textManager.entities.BillingItem;
import de.phsoftware.textManager.entities.Customer;
import de.phsoftware.textManager.entities.Setting;

public class PDFCreator extends NotifyingThread {
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
	new PDFCreator(new Bill().setBillNo("unique").setMonth(4).setYear(2012)
		.setCustomer(new ObjectId("4fad540444ae015226ff21e3"))).start();
    }

    public PDFCreator(Bill bill) throws IOException, InterruptedException {
	this.bill = bill;
	this.month = bill.getMonth();
	this.year = bill.getYear();
	this.customer = Customer.find(bill.getCustomer());
    }

    @SuppressWarnings("deprecation")
    private void preparePDF() {
	try {
	    File path = new File(System.getProperty("user.dir"), "template");
	    File template = new File(path, Setting.find("template").getValue());

	    Velocity.setProperty("file.resource.loader.path",
		    path.getAbsolutePath());
	    Velocity.init();
	    VelocityContext ctx = new VelocityContext();

	    // User data/Settings
	    for (Setting setting : ds.find(Setting.class).asList()) {
		ctx.put(setting.getKey(), setting.getValue());
	    }

	    ctx.put("number",
		    NumberFormat.getNumberInstance(new Locale(Setting.find(
			    "locale").getValue())));
	    // TODO update schema to have separate first and lastname
	    // Customer data
	    ctx.put("customer", customer);

	    // General data
	    ctx.put("month", new DateFormatSymbols().getMonths()[month]);
	    ctx.put("math", new MathTool());
	    // Billing data
	    ctx.put("allItems", BillingItem.find(customer.getId(), year, month));
	    ctx.put("billNo", bill.getBillNo());

	    StringWriter writer = new StringWriter();
	    Velocity.mergeTemplate(template.getName(), ctx, writer);
	    File filledTemplate = new File(path, bill.getBillNo() + ".tex");
	    FileUtils.writeStringToFile(filledTemplate, writer.toString(),
		    "ISO-8859-1");

	    ProcessBuilder pdfLatex = new ProcessBuilder(Setting.find(
		    "pdfLatex").getValue(), "-interaction nonstopmode",
		    "-output-format pdf", filledTemplate.toString());

	    // Saving template file (just in case it may be needed later
	    GridFSFile texFile = tex.createFile(filledTemplate);
	    texFile.put("month", month);
	    texFile.put("year", year);
	    texFile.put("customerId", customer.getId());
	    texFile.save();

	    pdfLatex.directory(path);
	    String pdfPath = filledTemplate.toString()
		    .replaceAll("tex$", "pdf");
	    if (0 == printOutputStream(pdfLatex.start())) {
		// display Bill in DocumentViewer
		new ProcessBuilder(Setting.find("pdfViewer").getValue(),
			pdfPath).start().waitFor();
		System.out.println(pdfPath);
		GridFSFile pdfFile = pdf.createFile(new File(pdfPath));
		pdfFile.put("month", month);
		pdfFile.put("year", year);
		pdfFile.put("customerId", customer.getId());
		pdf.remove(QueryBuilder.start("month").is(month).and("year")
			.is(year).and("customerId").is(customer.getId()).get());
		pdfFile.save();
		File[] files = path
			.listFiles((FileFilter) new WildcardFileFilter(bill
				.getBillNo() + ".*"));
		for (File file : files) {
		    FileUtils.forceDelete(file);
		}
	    } else {
		new JOptionPane(
			"Bei der Erstellung der Rechnung ist ein Fehler aufgetreten. Es wurde keine Rechnung erstellt.\n Bitte Schauen sie in die Logdatei für nähere Fehlerinformationen.",
			JOptionPane.ERROR_MESSAGE).setVisible(true);
	    }
	} catch (IOException e) {
	    Throwables.propagate(e);
	} catch (InterruptedException e) {
	    Throwables.propagate(e);
	}
    }

    private int printOutputStream(Process proc) throws IOException {
	InputStream is = proc.getInputStream();
	try {
	    Thread writer = new WriteStream(is);
	    writer.start();
	    int rc = proc.waitFor();
	    if (writer.isAlive()) {
		Thread.sleep(1000);
		writer.interrupt();
	    }
	    System.out.println("Return code:" + rc);
	    return rc;
	} catch (InterruptedException e) {
	    return -1;
	} finally {
	    proc.destroy();
	    System.out.println("creation interrupted");
	    is.close();
	}
    }

    @Override
    protected void doRun() {
	Thread.currentThread().setName("Pdf Creator:" + bill.getBillNo());
	preparePDF();
    }

    private class WriteStream extends Thread {
	private final InputStream is;

	public WriteStream(InputStream is) {
	    this.is = is;
	}

	@Override
	public void run() {
	    while (true) {
		try {
		    for (int i = 0; i < is.available(); i++) {
			int c = is.read();
			if (c == -1) {
			    return;
			}
			System.out.print((char) c);
		    }
		    Thread.sleep(10);
		    if (isInterrupted()) {
			return;
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		    return;
		} catch (InterruptedException e) {
		    e.printStackTrace();
		    return;
		}
	    }
	}

    }
}
