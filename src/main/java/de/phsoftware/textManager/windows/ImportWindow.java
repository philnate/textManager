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
package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import de.phsoftware.textManager.windows.components.BillingItemTable;

public class ImportWindow extends WindowAdapter {
    private JFrame frame;
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
	EventQueue.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    debug = true;
		    new ImportWindow();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the window.
     */
    public ImportWindow() {
	initialize();
	frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	frame = new JFrame();
	frame.setBounds(100, 100, 1000, 800);
	frame.getContentPane().setLayout(
		new MigLayout("", "[grow]", "[grow][][grow]"));

	JButton read = new JButton(getCaption("iw.tooltip.button.read"));
	JButton imprt = new JButton(getCaption("iw.tooltip.button.import"));

	frame.getContentPane().add(read, "cell 0 1");
	frame.getContentPane().add(imprt, "cell 0 1");

	JTextArea input = new JTextArea();
	input.setRows(20);
	frame.getContentPane().add(input, "flowx,cell 0 0,growx");

	JScrollPane pane = new JScrollPane();
	BillingItemTable table = new BillingItemTable(frame);
	table.setContextMenuEnabled(false);
	pane.setViewportView(table);
	frame.getContentPane().add(pane, "growy, cell 0 2,growx");
    }
}
