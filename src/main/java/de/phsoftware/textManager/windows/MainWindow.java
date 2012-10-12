package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.DB.pdf;
import static de.phsoftware.textManager.utils.I18N.getCaption;
import static de.phsoftware.textManager.utils.I18N.getCaptions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import com.mongodb.MongoException.DuplicateKey;
import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

import de.phsoftware.textManager.entities.Bill;
import de.phsoftware.textManager.entities.BillingItem;
import de.phsoftware.textManager.entities.Customer;
import de.phsoftware.textManager.entities.Document;
import de.phsoftware.textManager.entities.Setting;
import de.phsoftware.textManager.updates.Updater;
import de.phsoftware.textManager.utils.DB;
import de.phsoftware.textManager.utils.FileDrop;
import de.phsoftware.textManager.utils.ImageRegistry;
import de.phsoftware.textManager.utils.NotifyingThread;
import de.phsoftware.textManager.utils.PDFCreator;
import de.phsoftware.textManager.utils.ThreadCompleteListener;
import de.phsoftware.textManager.windows.helper.DocXFileChooser;
import de.phsoftware.textManager.windows.helper.DocumentListRenderer;

public class MainWindow {

    private JFrame frame;
    private JTable billLines;
    private DefaultTableModel model;
    private JScrollPane jScrollPane;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private ChangeListener changeListener;
    private List<BillingItem> curBill;
    private JComboBox customers;
    private JTextField billNo;
    private Bill bill;
    private JButton build;
    private JButton view;
    private Thread runningThread;

    /**
     * on value change load different bill into table
     * 
     * @author philnate
     * 
     */
    private class ChangeListener implements PropertyChangeListener,
	    ItemListener {

	public void itemStateChanged(ItemEvent arg0) {
	    fillTableModel();
	}

	public void propertyChange(PropertyChangeEvent evt) {
	    fillTableModel();
	}

    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
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

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	changeListener = new ChangeListener();

	frame = new JFrame();
	frame.setBounds(100, 100, 1197, 500);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(
		new MigLayout("", "[grow]", "[][grow]"));

	customers = new JComboBox();
	customers.addItemListener(changeListener);

	frame.getContentPane().add(customers, "flowx,cell 0 0,growx");

	jScrollPane = new JScrollPane();
	billLines = new JTable() {
	    private static final long serialVersionUID = 2641781237435176875L;

	    @Override
	    public boolean isCellEditable(int rowIndex, int colIndex) {
		if (4 == colIndex) {
		    return (Boolean) dataModel.getValueAt(rowIndex, 3);
		} else {
		    return true;
		}
	    }
	};
	billLines.setRowHeight(24);
	billLines.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	DocumentListRenderer docList = new DocumentListRenderer(frame);
	billLines.setDefaultRenderer(List.class, docList);
	billLines.setDefaultEditor(List.class, docList);

	jScrollPane.setViewportView(billLines);
	frame.getContentPane().add(jScrollPane, "cell 0 1,grow");

	// for each file added through drag&drop create a new lineItem
	new FileDrop(jScrollPane, new FileDrop.Listener() {

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
	    public void actionPerformed(ActionEvent arg0) {
		addNewBillingItem();
	    }
	});

	frame.getContentPane().add(btnAddLine, "cell 0 0");

	JButton btnMassAdd = new JButton();
	btnMassAdd.setIcon(ImageRegistry.getImage("load_all.gif"));
	btnMassAdd.setToolTipText(getCaption("mw.tooltip.massAdd"));
	btnMassAdd.addActionListener(new ActionListener() {

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

	    public void removeUpdate(DocumentEvent arg0) {
		setButtonStates();
	    }

	    public void insertUpdate(DocumentEvent arg0) {
		setButtonStates();
	    }

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
			    JOptionPane.showMessageDialog(
				    frame,
				    String.format(
					    getCaption("mw.error.billNoUsed.msg"),
					    billNo.getText()),
				    getCaption("mw.dialog.error.billNoBlank.title"),
				    JOptionPane.ERROR_MESSAGE);
			    return;
			}
			PDFCreator pdf = new PDFCreator(bill);
			pdf.addListener(new ThreadCompleteListener() {

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

	JMenuBar menuBar = new JMenuBar();
	frame.setJMenuBar(menuBar);

	JMenu menu = new JMenu(getCaption("mw.menu.edit"));
	JMenuItem itemCust = new JMenuItem(getCaption("mw.menu.edit.customer"));
	itemCust.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		new CustomerWindow(customers);
	    }
	});
	menu.add(itemCust);

	JMenuItem itemSetting = new JMenuItem(
		getCaption("mw.menu.edit.settings"));
	itemSetting.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		new SettingWindow();
	    }
	});
	menu.add(itemSetting);

	menuBar.add(menu);

	CustomerWindow.loadCustomer(customers);

	model = new tmTableModel(getCaptions("mw.tableheader", "title", "wc",
		"cw", "fixPrice", "total", "documents"));
	fillTableModel();
	billLines.setModel(model);

	view = new JButton();
	view.setToolTipText(getCaption("mw.tooltip.view"));
	view.setIcon(ImageRegistry.getImage("view.gif"));
	view.setEnabled(false);
	view.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		File file = new File(System.getProperty("user.dir"), String
			.format("template/%s.tmp.pdf", billNo.getText()));
		try {
		    pdf.findOne(billNo.getText() + ".pdf").writeTo(file);
		    new ProcessBuilder(Setting.find("pdfViewer").getValue(),
			    file.getAbsolutePath()).start().waitFor();
		    file.delete();
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}
	    }
	});
	frame.getContentPane().add(view, "cell 0 0");
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
	billLines.getColumnModel().getColumn(0).setPreferredWidth(400);
	billLines.getColumnModel().getColumn(5).setPreferredWidth(400);

	// Menu for Row actions, e.g. delete row
	billLines.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
		    JPopupMenu menu = new JPopupMenu();
		    JMenuItem delete = new JMenuItem(
			    getCaption("mw.itemmenu.delete"));
		    delete.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
			    if (JOptionPane.showConfirmDialog(frame,
				    getCaption("mw.dialog.itemdelete.title")) == JOptionPane.YES_OPTION) {
				System.out.println("delete");
				int row = billLines.rowAtPoint(e.getPoint());
				((BillingItem) ((Vector) model.getDataVector()
					.get(row)).get(0)).delete();
				model.removeRow(row);
			    }
			}
		    });
		    menu.add(delete);
		    menu.show(billLines, e.getX(), e.getY());
		}
	    }
	});

    }

    /**
     * Creates a new BillingItem and returns it. BillingItem is initialized with
     * current selected customer, month and year.
     * 
     * @return BillingItem newly created BillingItem with basic Initialization
     */
    private BillingItem addNewBillingItem() {
	BillingItem item = new BillingItem()
		.setCustomerId(((Customer) customers.getSelectedItem()).getId())
		.setMonth(monthChooser.getMonth())
		.setYear(yearChooser.getYear());
	model.addRow(new Object[] { item });
	item.save();
	return item;
    }

    private BillingItem addNewBillingItem(Document document) {
	BillingItem item = addNewBillingItem().addDocument(document);
	item.save();
	return item;
    }

    private void fillTableModel() {
	if (null == model) {
	    return;
	}
	model.setRowCount(0);
	if (null == customers.getSelectedItem()) {
	    return;
	}
	curBill = BillingItem.find(
		((Customer) customers.getSelectedItem()).getId(),
		yearChooser.getYear(), monthChooser.getMonth());
	for (BillingItem item : curBill) {
	    model.addRow(new Object[] { item });
	}

	bill = checkBillExists(monthChooser.getMonth(), yearChooser.getYear(),
		((Customer) customers.getSelectedItem()));
	billNo.setText(bill.getBillNo());
    }

    private class tmTableModel extends DefaultTableModel {

	private static final long serialVersionUID = -1178926028075689166L;

	public tmTableModel(String[] columnNames) {
	    super(columnNames, 0);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
	    switch (columnIndex) {
	    case 0:
		return String.class;
	    case 1:
		return Integer.class;
	    case 3:
		return Boolean.class;
	    case 2: /* fallthrough */
	    case 4:
		return Double.class;
	    case 5:
		return List.class;
	    }
	    return getValueAt(0, columnIndex).getClass();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
	    @SuppressWarnings("rawtypes")
	    BillingItem item = (BillingItem) ((Vector) dataVector
		    .elementAt(rowIndex)).get(0);
	    switch (columnIndex) {
	    case 0:
		return item.getTitle();
	    case 1:
		return item.getWordCount();
	    case 2:
		return item.getCentPerWord();
	    case 3:
		return item.isFixedPrice();
	    case 4:
		return item.getTotal();
	    case 5:
		return item.getDocuments();
	    default:
		throw new IllegalArgumentException(
			"Got columnIndex out of bounds");
	    }
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
	    @SuppressWarnings("rawtypes")
	    BillingItem item = (BillingItem) ((Vector) dataVector
		    .elementAt(rowIndex)).get(0);
	    switch (columnIndex) {
	    case 0:
		item.setTitle((String) value);
		break;
	    case 1:
		item.setWordCount((Integer) value);
		if (!item.isFixedPrice()) {
		    setValueAt(item.getWordCount() * item.getCentPerWord(),
			    rowIndex, 4);
		    fireTableCellUpdated(rowIndex, 4);
		    return;// no need to save twice
		}
		break;
	    case 2:
		item.setCentPerWord((Double) value);
		if (!item.isFixedPrice()) {
		    setValueAt(item.getWordCount() * item.getCentPerWord(),
			    rowIndex, 4);
		    fireTableCellUpdated(rowIndex, 4);
		    return;// no need to save twice
		}
		break;
	    case 3:
		item.setFixedPrice((Boolean) value);
		break;
	    case 4:
		item.setTotal((Double) value);
		break;
	    case 5:
		if (null != value && value.getClass().isArray()) {
		    for (File file : (File[]) value) {
			item.addDocument(Document.loadAndSave(file));
		    }
		} else if (value instanceof ObjectId) {
		    item.removeDocument((ObjectId) value);
		}
		break;
	    default:
		throw new IllegalArgumentException(
			"Got columnIndex out of bounds");
	    }
	    item.save();
	}
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
