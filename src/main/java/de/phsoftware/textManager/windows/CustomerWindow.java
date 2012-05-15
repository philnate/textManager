package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.DB.ds;
import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.EventQueue;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.phsoftware.textManager.entities.Customer;
import de.phsoftware.textManager.utils.ImageRegistry;

public class CustomerWindow extends WindowAdapter {

    private JFrame frame;
    private JTextField textContact;
    private JTextField textStreet;
    private JTextField textZip;
    private JTextField textCity;
    private JTextField textNr;
    private JComboBox customers;
    private JTextField textCompanyName;
    private JButton btnSave;
    private JComboBox feMale;
    private final JComboBox refresh;
    // tells what behavior to show on closing
    private static boolean debug = false;

    private static final String empty = "";

    /**
     * check on closing that no unsaved work is there
     */
    @Override
    public void windowClosing(WindowEvent evt) {
	if (textCity.isEnabled()) {
	    switch (JOptionPane.showConfirmDialog(null,
		    getCaption("cw.dialog.close.msg"),
		    getCaption("cw.dialog.close.title"),
		    JOptionPane.YES_NO_CANCEL_OPTION)) {
	    case JOptionPane.CANCEL_OPTION:
		return;
	    case JOptionPane.YES_OPTION:
		// don't close when saving wasn't possible due to empty fields
		// (after trim)
		if (false == save()) {
		    return;
		}
		break;
	    case JOptionPane.NO_OPTION:
		/* noop */
		break;
	    }
	}
	frame.dispose();
	loadCustomer(refresh);
	if (debug) {
	    // on debug this will exit the window and app
	    System.exit(0);
	}
    }

    /**
     * Launch the Window, debugging only
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    debug = true;
		    new CustomerWindow(null);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the window.
     */
    public CustomerWindow(JComboBox refresh) {
	this.refresh = refresh;
	initialize();
	frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	frame = new JFrame();
	frame.setBounds(100, 100, 829, 251);
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(this);
	frame.getContentPane().setLayout(
		new MigLayout("", "[][grow][117.00px,grow][grow]",
			"[24px][66.00px][][][]"));

	JLabel lblcustomer = new JLabel(getCaption("cw.label.customer"));
	frame.getContentPane().add(lblcustomer, "cell 0 0,alignx left");

	customers = new JComboBox();

	frame.getContentPane()
		.add(customers, "flowx,cell 1 0,growx,aligny top");

	JButton btnAddCustomer = new JButton();
	btnAddCustomer.setIcon(ImageRegistry.getImage("load.gif"));
	btnAddCustomer.setToolTipText(getCaption("cw.tooltip.button.add"));

	frame.getContentPane().add(btnAddCustomer,
		"flowx,cell 2 0,alignx left,growy");

	JLabel lblCompanyName = new JLabel(getCaption("cw.label.company"));
	frame.getContentPane().add(lblCompanyName, "cell 0 1");

	textCompanyName = new JTextField();
	frame.getContentPane().add(textCompanyName,
		"cell 1 1,growx,aligny center");

	JLabel lblStreet = new JLabel(getCaption("cw.label.street"));
	frame.getContentPane().add(lblStreet, "cell 0 2,alignx left");

	textStreet = new JTextField();
	frame.getContentPane().add(textStreet, "cell 1 2,growx");
	textStreet.setColumns(10);

	JLabel lblNr = new JLabel(getCaption("cw.label.streetNo"));
	frame.getContentPane().add(lblNr, "cell 2 2,alignx left");

	textNr = new JTextField();
	frame.getContentPane().add(textNr, "cell 3 2,growx");
	textNr.setColumns(10);

	JLabel lblZip = new JLabel(getCaption("cw.label.zip"));
	frame.getContentPane().add(lblZip, "cell 0 3,alignx left");

	textZip = new JTextField();
	frame.getContentPane().add(textZip, "cell 1 3,growx");
	textZip.setColumns(10);

	JLabel lblCity = new JLabel(getCaption("cw.label.city"));
	frame.getContentPane().add(lblCity, "cell 2 3,alignx left");

	textCity = new JTextField();
	frame.getContentPane().add(textCity, "cell 3 3,growx");
	textCity.setColumns(10);

	JLabel lblContact = new JLabel(getCaption("cw.label.contact"));
	frame.getContentPane().add(lblContact, "cell 0 4,alignx trailing");

	btnSave = new JButton();
	btnSave.setIcon(ImageRegistry.getImage("save.gif"));
	btnSave.setToolTipText(getCaption("cw.tooltip.button.save"));

	frame.getContentPane().add(btnSave, "cell 2 0");

	feMale = new JComboBox();
	feMale.setModel(new DefaultComboBoxModel(new String[] {
		getCaption("gender.male"), getCaption("gender.female") }));
	frame.getContentPane().add(feMale, "cell 1 4,growx");

	textContact = new JTextField();
	frame.getContentPane().add(textContact, "cell 2 4,growx");
	textContact.setColumns(10);

	/**
	 * loads selected Customer data into fields
	 */
	customers.addItemListener(new ItemListener() {

	    public void itemStateChanged(ItemEvent arg0) {
		if (arg0.getStateChange() == ItemEvent.SELECTED) {
		    Customer c = (Customer) customers.getSelectedItem();
		    if (null == c) {
			return;
		    }
		    textCity.setText(c.getCity());
		    feMale.setSelectedIndex(c.isMale() ? 0 : 1);
		    textContact.setText(c.getContactName());
		    textCompanyName.setText(c.getCompanyName());
		    textStreet.setText(c.getStreet());
		    textNr.setText(c.getStreetNo());
		    textZip.setText(c.getZip());
		}
	    }
	});
	// needs to be called after registering listener as else on startup it
	// will be empty
	loadCustomer(customers);
	/**
	 * sets up everything so that a new customer can be created
	 */
	btnAddCustomer.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		// clear fields
		textCity.setText(empty);
		feMale.setSelectedIndex(0);
		textContact.setText(empty);
		textCompanyName.setText(empty);
		textStreet.setText(empty);
		textNr.setText(empty);
		textZip.setText(empty);
		// enable all
		setEnabled(true);
	    }
	});

	/**
	 * saves newly created customer and disables everything
	 */
	btnSave.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		save();
	    }
	});

	setEnabled(false);
    }

    /**
     * saves customer data which was inserted. Will not save the customer when
     * something isn't filled in (whitespaces are trimmed)
     * 
     * @return boolean which tells if the Customer was saved (true) or not
     *         (false)
     */
    private boolean save() {
	if (0 == textCompanyName.getText().trim().length()
		|| 0 == textContact.getText().trim().length()
		|| 0 == textStreet.getText().trim().length()
		|| 0 == textNr.getText().trim().length()
		|| 0 == textZip.getText().trim().length()
		|| 0 == textCity.getText().trim().length()) {
	    // don't save if there's something not filled in
	    JOptionPane.showMessageDialog(null,
		    getCaption("cw.dialog.save.msg"),
		    getCaption("cw.dialog.save.title"),
		    JOptionPane.WARNING_MESSAGE);
	    return false;
	}
	new Customer().setCompanyName(textCompanyName.getText())
		.setContactName(textContact.getText())
		.setMale(feMale.getSelectedIndex() == 0 ? true : false)
		.setStreet(textStreet.getText()).setStreetNo(textNr.getText())
		.setZip(textZip.getText()).setCity(textCity.getText()).save();
	setEnabled(false);
	loadCustomer(customers);
	return true;
    }

    /**
     * enables or disables all important components
     * 
     * @param enabled
     */
    private void setEnabled(boolean enabled) {
	textCity.setEnabled(enabled);
	textContact.setEnabled(enabled);
	textCompanyName.setEnabled(enabled);
	textNr.setEnabled(enabled);
	textStreet.setEnabled(enabled);
	textZip.setEnabled(enabled);
    }

    /**
     * loads all customers currently stored in collection and shows their
     * heading in Combobox
     */
    public static void loadCustomer(JComboBox jcb) {
	if (null == jcb) {
	    return;
	}
	jcb.removeAllItems();
	Iterator<Customer> it = ds.find(Customer.class)
		.order("companyName, contactName").asList().iterator();
	while (it.hasNext()) {
	    jcb.addItem(it.next());
	}
    }
}
