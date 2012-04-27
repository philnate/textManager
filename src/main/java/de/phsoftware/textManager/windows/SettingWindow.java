package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.DB.ds;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import com.google.common.collect.Maps;

import de.phsoftware.textManager.entities.Setting;

public class SettingWindow {

    private JFrame frame;
    private final Map<String, JTextField> settings = Maps.newHashMap();
    private JButton btnV;
    private JButton btnX;

    /**
     * Launch the Window, debugging only
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    new SettingWindow();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the window.
     */
    public SettingWindow() {
	initialize();
	frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	frame = new JFrame();
	frame.setBounds(100, 100, 857, 624);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().setLayout(
		new MigLayout("", "[][grow]", "[][][][][][][][][][]"));

	JLabel lblNewLabel = new JLabel("Firmenname:");
	frame.getContentPane().add(lblNewLabel, "cell 0 0,alignx trailing");

	JTextField company = new JTextField();
	frame.getContentPane().add(company, "cell 1 0,growx");
	company.setColumns(10);
	settings.put("company", company);

	JLabel lblNewLabel_1 = new JLabel("Kontakt:");
	frame.getContentPane().add(lblNewLabel_1, "cell 0 1,alignx trailing");

	JTextField contact = new JTextField();
	frame.getContentPane().add(contact, "cell 1 1,growx");
	contact.setColumns(10);
	settings.put("contact", contact);

	JLabel lblNewLabel_2 = new JLabel("Straße:");
	frame.getContentPane().add(lblNewLabel_2, "cell 0 2,alignx trailing");

	JTextField street = new JTextField();
	frame.getContentPane().add(street, "flowx,cell 1 2,growx");
	street.setColumns(10);
	settings.put("street", street);

	JLabel lblNewLabel_4 = new JLabel("PLZ:");
	frame.getContentPane().add(lblNewLabel_4, "cell 0 3,alignx trailing");

	JTextField zip = new JTextField();
	frame.getContentPane().add(zip, "flowx,cell 1 3,growx");
	zip.setColumns(10);
	settings.put("zip", zip);

	JLabel lblNewLabel_6 = new JLabel("EMail:");
	frame.getContentPane().add(lblNewLabel_6, "cell 0 4,alignx trailing");

	JTextField email = new JTextField();
	frame.getContentPane().add(email, "cell 1 4,growx");
	email.setColumns(10);
	settings.put("email", email);

	JLabel lblNewLabel_7 = new JLabel("Telefon:");
	frame.getContentPane().add(lblNewLabel_7, "cell 0 5,alignx trailing");

	JTextField phone = new JTextField();
	frame.getContentPane().add(phone, "cell 1 5,growx");
	phone.setColumns(10);
	settings.put("phone", phone);

	JLabel lblNewLabel_3 = new JLabel("Nr:");
	frame.getContentPane().add(lblNewLabel_3, "cell 1 2,alignx trailing");

	JTextField streetNo = new JTextField();
	frame.getContentPane().add(streetNo, "cell 1 2,growx");
	streetNo.setColumns(10);
	settings.put("streetNo", streetNo);

	JLabel lblNewLabel_5 = new JLabel("Stadt:");
	frame.getContentPane().add(lblNewLabel_5, "cell 1 3,alignx trailing");

	JTextField city = new JTextField();
	frame.getContentPane().add(city, "cell 1 3,growx");
	city.setColumns(10);
	settings.put("city", city);

	JLabel lblNewLabel_8 = new JLabel("Pfad pdfLatex:");
	frame.getContentPane().add(lblNewLabel_8, "cell 0 6,alignx trailing");

	JTextField pdfLatex = new JTextField();
	frame.getContentPane().add(pdfLatex, "flowx,cell 1 6,growx");
	settings.put("pdfLatex", pdfLatex);

	JLabel lblNewLabel_9 = new JLabel("Template Verzeichnis:");
	frame.getContentPane().add(lblNewLabel_9, "cell 0 7,alignx trailing");

	JTextField template = new JTextField();
	frame.getContentPane().add(template, "cell 1 7,growx");
	settings.put("template", template);

	JLabel lblNewLabel_10 = new JLabel("PDF Viewer:");
	frame.getContentPane().add(lblNewLabel_10, "cell 0 8,alignx trailing");

	JTextField pdfViewer = new JTextField();
	frame.getContentPane().add(pdfViewer, "cell 1 8,growx");
	settings.put("pdfViewer", pdfViewer);

	btnV = new JButton("v/");
	frame.getContentPane().add(btnV, "flowx,cell 1 9");
	btnV.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		safeSettings();
	    }
	});

	btnX = new JButton("X");
	frame.getContentPane().add(btnX, "cell 1 9");
	btnX.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		frame.dispose();
	    }
	});

	loadSettings();
    }

    public void safeSettings() {
	Iterator<Entry<String, JTextField>> it = settings.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, JTextField> entry = it.next();
	    ds.save(new Setting(entry.getKey(), entry.getValue().getText()));
	}
    }

    public void loadSettings() {
	Iterator<Entry<String, JTextField>> it = settings.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, JTextField> entry = it.next();
	    entry.getValue().setText(
		    Setting.findSetting(entry.getKey()).getValue());
	}
    }
}
