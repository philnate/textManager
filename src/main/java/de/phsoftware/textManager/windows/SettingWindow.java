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
package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
	for (Entry<String, JTextField> entry : settings.entrySet()) {
	    new Setting(entry.getKey(), entry.getValue().getText()).save();
	}
    }

    private void loadSettings() {
	for (Entry<String, JTextField> entry : settings.entrySet()) {
	    entry.getValue().setText(Setting.find(entry.getKey()).getValue());
	}
    }
}
