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

import static java.lang.String.format;
import static me.philnate.textmanager.utils.DB.pdf;
import static me.philnate.textmanager.utils.I18N.getCaption;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import me.philnate.textmanager.entities.Bill;
import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.entities.Customer;
import me.philnate.textmanager.entities.Document;
import me.philnate.textmanager.entities.Setting;
import me.philnate.textmanager.updates.Updater;
import me.philnate.textmanager.utils.DB;
import me.philnate.textmanager.utils.FileDrop;
import me.philnate.textmanager.utils.ImageRegistry;
import me.philnate.textmanager.utils.NotifyingThread;
import me.philnate.textmanager.utils.PDFCreator;
import me.philnate.textmanager.utils.ThreadCompleteListener;
import me.philnate.textmanager.windows.ImportWindow.ImportListener;
import me.philnate.textmanager.windows.components.BillingItemTable;
import me.philnate.textmanager.windows.components.CustomerComboBox;
import me.philnate.textmanager.windows.helper.DocXFileChooser;
import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoException.DuplicateKey;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

public class MainWindow {

    private JFrame frame;
    private BillingItemTable billLines;
    private JScrollPane jScrollPane;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private ChangeListener changeListener;
    private List<BillingItem> curBill;
    private CustomerComboBox customers;
    private JTextField billNo;
    private Bill bill;
    private JButton build;
    private JButton view;
    private Thread runningThread;

    private static Logger LOG = LoggerFactory.getLogger(MainWindow.class);

    /**
     * on value change load different bill into table
     * 
     * @author philnate
     * 
     */
    private class ChangeListener implements PropertyChangeListener,
	    ItemListener {

	@Override
	public void itemStateChanged(ItemEvent arg0) {
	    fillTableModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	    fillTableModel();
	}

    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    MainWindow window = new MainWindow();
		    window.frame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the window.
     */
    public MainWindow() {
	Updater.checkUpdateNeeded();
	initialize();
    }

    public void show() {
	frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	changeListener = new ChangeListener();

	frame = new JFrame();
	frame.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		Starter.shutdown();
	    }
	});
	frame.setBounds(100, 100, 1197, 500);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(
		new MigLayout("", "[grow]", "[][grow]"));

	customers = new CustomerComboBox();
	customers.addItemListener(changeListener);

	frame.getContentPane().add(customers, "flowx,cell 0 0,growx");

	jScrollPane = new JScrollPane();
	billLines = new BillingItemTable(frame, true);

	jScrollPane.setViewportView(billLines);
	frame.getContentPane().add(jScrollPane, "cell 0 1,grow");

	// for each file added through drag&drop create a new lineItem
	new FileDrop(jScrollPane, new FileDrop.Listener() {

	    @Override
	    public void filesDropped(File[] files) {
		for (File file : files) {
		    addNewBillingItem(Document.loadAndSave(file));
		}
	    }
	});

	monthChooser = new JMonthChooser();
	monthChooser.addPropertyChangeListener(changeListener);
	frame.getContentPane().add(monthChooser, "cell 0 0");

	yearChooser = new JYearChooser();
	yearChooser.addPropertyChangeListener(changeListener);
	frame.getContentPane().add(yearChooser, "cell 0 0");

	JButton btnAddLine = new JButton();
	btnAddLine.setIcon(ImageRegistry.getImage("load.gif"));
	btnAddLine.setToolTipText(getCaption("mw.tooltip.add"));
	btnAddLine.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		addNewBillingItem();
	    }
	});

	frame.getContentPane().add(btnAddLine, "cell 0 0");

	JButton btnMassAdd = new JButton();
	btnMassAdd.setIcon(ImageRegistry.getImage("load_all.gif"));
	btnMassAdd.setToolTipText(getCaption("mw.tooltip.massAdd"));
	btnMassAdd.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		JFileChooser file = new DocXFileChooser();
		switch (file.showOpenDialog(frame)) {
		case JFileChooser.APPROVE_OPTION:
		    File[] files = file.getSelectedFiles();
		    if (null != files) {
			for (File fl : files) {
			    addNewBillingItem(Document.loadAndSave(fl));
			}
		    }
		    break;
		default:
		    return;
		}
	    }
	});

	frame.getContentPane().add(btnMassAdd, "cell 0 0");

	billNo = new JTextField();
	// enable/disable build button based upon text in billNo
	billNo.getDocument().addDocumentListener(new DocumentListener() {

	    @Override
	    public void removeUpdate(DocumentEvent arg0) {
		setButtonStates();
	    }

	    @Override
	    public void insertUpdate(DocumentEvent arg0) {
		setButtonStates();
	    }

	    @Override
	    public void changedUpdate(DocumentEvent arg0) {
		setButtonStates();
	    }

	    private void setButtonStates() {
		boolean notBlank = StringUtils.isNotBlank(billNo.getText());
		build.setEnabled(notBlank);
		view.setEnabled(pdf.find(billNo.getText() + ".pdf").size() == 1);
	    }
	});
	frame.getContentPane().add(billNo, "cell 0 0");
	billNo.setColumns(10);

	build = new JButton();
	build.setEnabled(false);// disable build Button until there's some
				// billNo entered
	build.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		if (runningThread == null) {
		    try {
			// check that billNo isn't empty or already used within
			// another Bill
			if (billNo.getText().trim().equals("")) {
			    JOptionPane
				    .showMessageDialog(
					    frame,
					    getCaption("mw.dialog.error.billNoBlank.msg"),
					    getCaption("mw.dialog.error.billNoBlank.title"),
					    JOptionPane.ERROR_MESSAGE);
			    return;
			}
			try {
			    bill.setBillNo(billNo.getText()).save();
			} catch (DuplicateKey e) {
			    // unset the internal value as this is already used
			    bill.setBillNo("");
			    JOptionPane
				    .showMessageDialog(
					    frame,
					    format(getCaption("mw.error.billNoUsed.msg"),
						    billNo.getText()),
					    getCaption("mw.dialog.error.billNoBlank.title"),
					    JOptionPane.ERROR_MESSAGE);
			    return;
			}
			PDFCreator pdf = new PDFCreator(bill);
			pdf.addListener(new ThreadCompleteListener() {

			    @Override
			    public void threadCompleted(
				    NotifyingThread notifyingThread) {
				build.setToolTipText(getCaption("mw.tooltip.build"));
				build.setIcon(ImageRegistry
					.getImage("build.png"));
				runningThread = null;
				view.setEnabled(DB.pdf.find(
					billNo.getText() + ".pdf").size() == 1);
			    }
			});
			runningThread = new Thread(pdf);
			runningThread.start();
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    build.setToolTipText(getCaption("mw.tooltip.build.cancel"));
		    build.setIcon(ImageRegistry.getImage("cancel.gif"));
		} else {
		    runningThread.interrupt();
		    runningThread = null;
		    build.setToolTipText(getCaption("mw.tooltip.build"));
		    build.setIcon(ImageRegistry.getImage("build.png"));

		}
	    }
	});
	build.setToolTipText(getCaption("mw.tooltip.build"));
	build.setIcon(ImageRegistry.getImage("build.png"));
	frame.getContentPane().add(build, "cell 0 0");

	view = new JButton();
	view.setToolTipText(getCaption("mw.tooltip.view"));
	view.setIcon(ImageRegistry.getImage("view.gif"));
	view.setEnabled(false);
	view.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		File file = new File(System.getProperty("user.dir"), format(
			"template/%s.tmp.pdf", billNo.getText()));
		try {
		    pdf.findOne(billNo.getText() + ".pdf").writeTo(file);
		    new ProcessBuilder(Setting.find("pdfViewer").getValue(),
			    file.getAbsolutePath()).start().waitFor();
		    file.delete();
		} catch (IOException | InterruptedException e1) {
		    // TODO Auto-generated catch block
		    LOG.warn("Error while building PDF", e1);
		}
	    }
	});
	frame.getContentPane().add(view, "cell 0 0");

	JMenuBar menuBar = new JMenuBar();
	frame.setJMenuBar(menuBar);

	JMenu menu = new JMenu(getCaption("mw.menu.edit"));
	JMenuItem itemCust = new JMenuItem(getCaption("mw.menu.edit.customer"));
	itemCust.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		new CustomerWindow(customers);
	    }
	});
	menu.add(itemCust);

	JMenuItem itemSetting = new JMenuItem(
		getCaption("mw.menu.edit.settings"));
	itemSetting.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		new SettingWindow();
	    }
	});
	menu.add(itemSetting);

	JMenuItem itemImport = new JMenuItem(getCaption("mw.menu.edit.import"));
	itemImport.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		new ImportWindow(new ImportListener() {

		    @Override
		    public void entriesImported(List<BillingItem> items) {
			for (BillingItem item : items) {
			    item.setCustomerId(
				    customers.getSelectedCustomer().getId())
				    .setMonth(monthChooser.getMonth())
				    .setYear(yearChooser.getYear());
			    item.save();
			    billLines.addRow(item);
			}
		    }
		}, frame);
	    }
	});
	menu.add(itemImport);

	menuBar.add(menu);

	customers.loadCustomer();
	fillTableModel();

	// DecimalFormat df = new DecimalFormat("#,##0.##"); // you shouldn't
	// // need more "#"
	// // to the left

	// billLines.getColumnModel().getColumn(4)
	// .setCellRenderer(NumberRenderer.getCurrencyRenderer());
	// billLines
	// .getColumnModel()
	// .getColumn(4)
	// .setCellEditor(
	// new DefaultCellEditor(new JFormattedTextField(df)));
    }

    /**
     * Creates a new BillingItem and returns it. BillingItem is initialized with
     * current selected customer, month and year.
     * 
     * @return BillingItem newly created BillingItem with basic Initialization
     */
    private BillingItem addNewBillingItem() {
	BillingItem item = new BillingItem()
		.setCustomerId(customers.getSelectedCustomer().getId())
		.setMonth(monthChooser.getMonth())
		.setYear(yearChooser.getYear());
	billLines.addRow(item);
	item.save();
	return item;
    }

    private BillingItem addNewBillingItem(Document document) {
	BillingItem item = addNewBillingItem().addDocument(document);
	item.save();
	return item;
    }

    private void fillTableModel() {
	if (null == billLines) {
	    return;
	}
	billLines.flushRows();
	Customer c = customers.getSelectedCustomer();
	if (null == c) {
	    return;
	}
	curBill = BillingItem.find(c.getId(), yearChooser.getYear(),
		monthChooser.getMonth());
	for (BillingItem item : curBill) {
	    billLines.addRow(item);
	}

	bill = checkBillExists(monthChooser.getMonth(), yearChooser.getYear(),
		c);
	billNo.setText(bill.getBillNo());
    }

    private Bill checkBillExists(int month, int year, Customer customer) {
	Bill bill = Bill.find(customer.getId(), year, month);
	if (bill == null) {
	    Bill b = new Bill().setMonth(month).setYear(year)
		    .setCustomer(customer.getId()).setId(new ObjectId());
	    b.save();
	    return b;
	} else {
	    return bill;
	}
    }
}
