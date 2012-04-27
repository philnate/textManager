package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.DB.ds;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

import org.bson.types.ObjectId;

import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

import de.phsoftware.textManager.entities.BillingItem;
import de.phsoftware.textManager.entities.Customer;
import de.phsoftware.textManager.entities.Document;

public class MainWindow {

    private JFrame frame;
    private JTable billLines;
    private DefaultTableModel model;
    private JScrollPane jScrollPane;
    private JMonthChooser monthChooser;
    private JYearChooser yearChooser;
    private PropertyChangeListener changeListener;
    private List<BillingItem> curBill;
    private JComboBox customers;

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
	initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	changeListener = new PropertyChangeListener() {
	    /**
	     * on value change load different bill
	     */
	    public void propertyChange(PropertyChangeEvent evt) {
		fillTableModel();
	    }
	};

	frame = new JFrame();
	frame.setBounds(100, 100, 1197, 500);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(
		new MigLayout("", "[grow]", "[][grow]"));

	customers = new JComboBox();
	customers.addPropertyChangeListener(changeListener);

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

	monthChooser = new JMonthChooser();
	monthChooser.addPropertyChangeListener(changeListener);
	frame.getContentPane().add(monthChooser, "cell 0 0");

	yearChooser = new JYearChooser();
	monthChooser.addPropertyChangeListener(changeListener);
	frame.getContentPane().add(yearChooser, "cell 0 0");

	JButton btnAddLine = new JButton("+");
	btnAddLine.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		addNewBillingItem();
	    }
	});
	frame.getContentPane().add(btnAddLine, "cell 0 0");

	JButton btnMassAdd = new JButton("/\\");
	btnMassAdd.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		JFileChooser file = new JFileChooser();
		file.setAcceptAllFileFilterUsed(false);
		file.setMultiSelectionEnabled(true);
		file.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file.addChoosableFileFilter(new FileFilter() {

		    @Override
		    public String getDescription() {
			return "Word .docx/.doc Datei";
		    }

		    @Override
		    public boolean accept(File arg0) {
			if (arg0.getName().endsWith(".docx")
				|| arg0.isDirectory()) {
			    return true;
			}
			return false;
		    }
		});
		switch (file.showOpenDialog(frame)) {
		case JFileChooser.APPROVE_OPTION:
		    File[] files = file.getSelectedFiles();
		    if (null != files) {
			for (File fl : files) {
			    addNewBillingItem().addDocument(
				    Document.loadAndSave(fl));
			}
		    }
		    break;
		default:
		    return;
		}
	    }
	});

	frame.getContentPane().add(btnMassAdd, "cell 0 0");

	JMenuBar menuBar = new JMenuBar();
	frame.setJMenuBar(menuBar);

	JMenu menu = new JMenu("?");
	JMenuItem itemCust = new JMenuItem("Kunden bearbeiten");
	itemCust.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		new CustomerWindow(customers);
	    }
	});
	menu.add(itemCust);

	JMenuItem itemSetting = new JMenuItem("Einstellungen");
	itemSetting.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		new SettingWindow();
	    }
	});
	menu.add(itemSetting);

	menuBar.add(menu);

	JMenu menu_1 = new JMenu("New m");
	menu.add(menu_1);

	CustomerWindow.loadCustomer(customers);

	model = new tmTableModel(new String[] { "Titel", "Wortzahl",
		"Cent/Wort", "Festpreis", "Gesamt", "Dokumente" });
	fillTableModel();
	billLines.setModel(model);
	billLines.getColumnModel().getColumn(0).setPreferredWidth(400);
	billLines.getColumnModel().getColumn(5).setPreferredWidth(400);

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
	return item;
    }

    private void fillTableModel() {
	model.setRowCount(0);
	if (null == customers.getSelectedItem()) {
	    return;
	}
	curBill = ds
		.createQuery(BillingItem.class)
		.filter("month", monthChooser.getMonth())
		.filter("year", yearChooser.getYear())
		.filter("customerId",
			((Customer) customers.getSelectedItem()).getId())
		.asList();
	Iterator<BillingItem> it = curBill.iterator();
	while (it.hasNext()) {
	    BillingItem item = it.next();
	    model.addRow(new Object[] { item });
	}
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
	    ds.save(item);
	}
    }
}
