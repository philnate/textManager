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

import static me.philnate.textmanager.utils.I18N.getCaption;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.utils.I18N;
import me.philnate.textmanager.windows.components.BillingItemTable;
import net.miginfocom.swing.MigLayout;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ImportWindow extends WindowAdapter {
    private JDialog frame;
    private static boolean debug = false;

    private JTextArea input;
    private JTextField regex;
    private final ImportListener listener;

    /**
     * Allows it to get notified if a import was initialized
     * 
     * @author user
     * 
     */
    protected interface ImportListener {
	/**
	 * will be called as soon as the actual import was started
	 * 
	 * @param items
	 *            list of imported items, read items do not contain any
	 *            relationship to customer, year, month...
	 */
	public void entriesImported(List<BillingItem> items);
    }

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
		    new ImportWindow(null, null);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the window.
     */
    public ImportWindow(ImportListener listener, JFrame parent) {
	initialize(parent);
	frame.setVisible(true);
	this.listener = listener;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(JFrame parent) {
	frame = new JDialog(parent);
	frame.setModal(true);
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(this);
	frame.setBounds(100, 100, 1000, 800);
	frame.getContentPane().setLayout(
		new MigLayout("", "[grow]", "[][][grow]"));

	JButton read = new JButton(getCaption("iw.tooltip.button.read"));
	JButton imprt = new JButton(getCaption("iw.tooltip.button.import"));

	frame.getContentPane().add(read, "cell 0 1");
	frame.getContentPane().add(imprt, "cell 0 1");

	regex = new JTextField(
		"[0-9]+\\s+(?<title>Texterstellung ID [0-9]+)\\s+Datum:.*(?<sum>[0-9]+,[0-9]+)\\s+\\k<sum>$");
	frame.getContentPane().add(regex, "cell 0 1,growx");

	input = new JTextArea();
	input.setRows(20);
	frame.getContentPane().add(new JScrollPane(input),
		"h 340px, cell 0 0, growx");

	JScrollPane pane = new JScrollPane();
	final BillingItemTable table = new BillingItemTable(frame);
	table.setContextMenuEnabled(false);
	pane.setViewportView(table);
	frame.getContentPane().add(pane, "growy, cell 0 2,growx");

	read.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		table.flushRows();
		table.addRows(readItems(input.getText().split("\\r?\\n"),
			regex.getText()));
	    }
	});

	imprt.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (listener != null) {
		    listener.entriesImported(readItems(
			    input.getText().split("\\r?\\n"), regex.getText()));
		}
		// close window after import
		frame.dispatchEvent(new WindowEvent(frame,
			WindowEvent.WINDOW_CLOSING));
	    }
	});
    }

    private List<BillingItem> readItems(String[] lines, String regex) {
	Pattern pattern = Pattern.compile(regex);
	List<BillingItem> items = Lists.newArrayListWithCapacity(lines.length);
	for (String line : lines) {
	    Optional<BillingItem> item = parseLine(line, pattern);
	    if (item.isPresent()) {
		items.add(item.get());
	    }
	}
	return items;
    }

    private Optional<BillingItem> parseLine(String line, Pattern pattern) {
	BillingItem item = new BillingItem();
	Matcher matcher = pattern.matcher(line);
	if (matcher.matches()) {
	    item.setTitle(getString(matcher, "title").or(""));
	    item.setTotal(getDouble(matcher, "sum").or(0d));
	    item.setWordCount(getInt(matcher, "wordCount").or(0));
	    Double pricePerWord = getDouble(matcher, "wordPrice").or(0d);
	    item.setFixedPrice(pricePerWord == 0);
	    item.setCentPerWord(pricePerWord);
	} else {
	    System.out.println(String.format(
		    "Could not parse line '%s' with regex '%s'", line,
		    pattern.pattern()));
	    return Optional.absent();
	}
	return Optional.of(item);
    }

    private Optional<String> getString(Matcher matcher, String group) {
	try {
	    return Optional.of(matcher.group(group));
	} catch (IllegalArgumentException e) {
	    return Optional.absent();
	}
    }

    private Optional<Integer> getInt(Matcher matcher, String group) {
	try {
	    return Optional.of(Integer.parseInt(matcher.group(group)));
	} catch (IllegalArgumentException e) {
	    if (NumberFormatException.class.isInstance(e)) {
		// TODO should be rather an alertbox
		System.out.println(String.format(
			"Could not parse %s to integer", matcher.group(group)));
	    }
	    return Optional.absent();
	}
    }

    private Optional<Double> getDouble(Matcher matcher, String group) {
	try {
	    return Optional.of(NumberFormat.getNumberInstance(I18N.getLocale())
		    .parse(matcher.group(group)).doubleValue());
	} catch (IllegalArgumentException | ParseException e) {
	    if (ParseException.class.isInstance(e)) {
		// TODO should be rather an alertbox
		System.out.println(String.format(
			"Could not parse %s to double", matcher.group(group)));
	    }
	    return Optional.absent();
	}
    }
}
