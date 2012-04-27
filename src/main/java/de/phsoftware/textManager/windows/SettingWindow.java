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

	createTextField("company", "cell 1 0,growx");

	JLabel lblNewLabel_1 = new JLabel("Kontakt:");
	frame.getContentPane().add(lblNewLabel_1, "cell 0 1,alignx trailing");

	createTextField("contact", "cell 1 1,growx");

	JLabel lblNewLabel_2 = new JLabel("Straße:");
	frame.getContentPane().add(lblNewLabel_2, "cell 0 2,alignx trailing");

	createTextField("street", "flowx,cell 1 2,growx");

	JLabel lblNewLabel_4 = new JLabel("PLZ:");
	frame.getContentPane().add(lblNewLabel_4, "cell 0 3,alignx trailing");

	createTextField("zip", "flowx,cell 1 3,growx");

	JLabel lblNewLabel_6 = new JLabel("EMail:");
	frame.getContentPane().add(lblNewLabel_6, "cell 0 4,alignx trailing");

	createTextField("email", "cell 1 4,growx");

	JLabel lblNewLabel_7 = new JLabel("Telefon:");
	frame.getContentPane().add(lblNewLabel_7, "cell 0 5,alignx trailing");

	createTextField("phone", "cell 1 5,growx");

	JLabel lblNewLabel_3 = new JLabel("Nr:");
	frame.getContentPane().add(lblNewLabel_3, "cell 1 2,alignx trailing");

	createTextField("streetNo", "cell 1 2,growx");

	JLabel lblNewLabel_5 = new JLabel("Stadt:");
	frame.getContentPane().add(lblNewLabel_5, "cell 1 3,alignx trailing");

	createTextField("city", "cell 1 3,growx");

	JLabel lblNewLabel_8 = new JLabel("Pfad pdfLatex:");
	frame.getContentPane().add(lblNewLabel_8, "cell 0 6,alignx trailing");

	createTextField("pdfLatex", "flowx,cell 1 6,growx");

	JLabel lblNewLabel_9 = new JLabel("Template Verzeichnis:");
	frame.getContentPane().add(lblNewLabel_9, "cell 0 7,alignx trailing");

	createTextField("template", "cell 1 7,growx");

	JLabel lblNewLabel_10 = new JLabel("PDF Viewer:");
	frame.getContentPane().add(lblNewLabel_10, "cell 0 8,alignx trailing");

	createTextField("pdfViewer", "cell 1 8,growx");

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

    private void createTextField(String name, String layout) {
	JTextField text = new JTextField();
	frame.getContentPane().add(text, layout);
	settings.put(name, text);
    }

    private void safeSettings() {
	Iterator<Entry<String, JTextField>> it = settings.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, JTextField> entry = it.next();
	    ds.save(new Setting(entry.getKey(), entry.getValue().getText()));
	}
    }

    private void loadSettings() {
	Iterator<Entry<String, JTextField>> it = settings.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, JTextField> entry = it.next();
	    entry.getValue().setText(
		    Setting.findSetting(entry.getKey()).getValue());
	}
    }
}
