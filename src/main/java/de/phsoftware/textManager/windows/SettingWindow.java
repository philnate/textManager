package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import de.phsoftware.textManager.utils.ImageRegistry;
import de.phsoftware.textManager.windows.helper.ContextMenuMouseListener;

public class SettingWindow extends WindowAdapter {

    private JFrame frame;
    private final Map<String, JTextField> settings = Maps.newHashMap();
    private JButton btnV;
    private JButton btnX;
    private static boolean debug = false;

    @Override
    public void windowClosing(WindowEvent evt) {
	if (debug) {
	    System.exit(0);
	} else {
	    frame.dispose();
	}
    }

    /**
     * Launch the Window, debugging only
     */
    public static void main(String[] args) {
	debug = true;
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
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(this);
	frame.getContentPane().setLayout(new MigLayout("", "[][grow]", ""));

	createTextField("company", null, null);
	createTextField("contact", null, null);
	createTextField("street", null, null);
	createTextField("streetNo", null, null);
	createTextField("zip", null, null);
	createTextField("city", null, null);
	createTextField("email", null, null);
	createTextField("phone", null, null);
	createTextField("taxNo", null, null);
	createTextField("bankName", null, null);
	createTextField("bankNo", null, null);
	createTextField("accountNo", null, null);
	createTextField("pdfLatex", null, null);
	createTextField("template", null, null);
	createTextField("pdfViewer", null, null);
	createTextField("locale", null, null);

	btnV = new JButton();
	btnV.setIcon(ImageRegistry.getImage("save.gif"));
	btnV.setToolTipText(getCaption("sw.tooltip.button.save"));
	frame.getContentPane().add(btnV, "flowx");
	btnV.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent e) {
		safeSettings();
	    }
	});

	btnX = new JButton();
	btnX.setIcon(ImageRegistry.getImage("cancel.gif"));
	btnX.setToolTipText(getCaption("sw.tooltip.button.cancel"));
	frame.getContentPane().add(btnX);
	btnX.addActionListener(new ActionListener() {

	    public void actionPerformed(ActionEvent arg0) {
		frame.dispose();
	    }
	});

	loadSettings();
    }

    private void createTextField(String name, String layoutTF, String layoutL) {
	JLabel label = new JLabel(getCaption("sw.label." + name));
	frame.getContentPane().add(label, (null == layoutTF) ? "" : layoutTF);
	JTextField text = new JTextField();
	text.addMouseListener(ContextMenuMouseListener.RIGHT_CLICK_MENU);
	frame.getContentPane().add(text,
		(null == layoutL) ? "growx,wrap" : layoutL);
	settings.put(name, text);
    }

    private void safeSettings() {
	Iterator<Entry<String, JTextField>> it = settings.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, JTextField> entry = it.next();
	    new Setting(entry.getKey(), entry.getValue().getText()).save();
	}
    }

    private void loadSettings() {
	Iterator<Entry<String, JTextField>> it = settings.entrySet().iterator();
	while (it.hasNext()) {
	    Entry<String, JTextField> entry = it.next();
	    entry.getValue().setText(
		    Setting.find(entry.getKey()).getValue());
	}
    }
}
