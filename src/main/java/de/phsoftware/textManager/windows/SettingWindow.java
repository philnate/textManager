package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.DB.ds;
import static de.phsoftware.textManager.utils.I18N.getCaption;

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

	createTextField("company", "cell 1 0,growx", "cell 0 0,alignx trailing");
	createTextField("contact", "cell 1 1,growx", "cell 0 1,alignx trailing");
	createTextField("street", "flowx,cell 1 2,growx",
		"cell 0 2,alignx trailing");
	createTextField("zip", "flowx,cell 1 3,growx",
		"cell 0 3,alignx trailing");
	createTextField("email", "cell 1 4,growx", "cell 0 4,alignx trailing");
	createTextField("phone", "cell 1 5,growx", "cell 0 5,alignx trailing");
	createTextField("streetNo", "cell 1 2,growx",
		"cell 1 2,alignx trailing");
	createTextField("city", "cell 1 3,growx", "cell 1 3,alignx trailing");
	createTextField("pdfLatex", "flowx,cell 1 6,growx",
		"cell 0 6,alignx trailing");
	createTextField("template", "cell 1 7,growx",
		"cell 0 7,alignx trailing");
	createTextField("pdfViewer", "cell 1 8,growx",
		"cell 0 8,alignx trailing");

	btnV = new JButton(getCaption("sw.button.save.label"));
	frame.getContentPane().add(btnV, "flowx,cell 1 9");
	btnV.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		safeSettings();
	    }
	});

	btnX = new JButton(getCaption("sw.button.cancel.label"));
	frame.getContentPane().add(btnX, "cell 1 9");
	btnX.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		frame.dispose();
	    }
	});

	loadSettings();
    }

    private void createTextField(String name, String layoutTF, String layoutL) {
	JLabel label = new JLabel(getCaption("sw.label." + name));
	frame.getContentPane().add(label, layoutL);
	JTextField text = new JTextField();
	frame.getContentPane().add(text, layoutTF);
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
