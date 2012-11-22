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
package de.phsoftware.textManager.windows.components;

import static de.phsoftware.textManager.utils.I18N.getCaption;
import static de.phsoftware.textManager.utils.I18N.getCaptions;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import de.phsoftware.textManager.entities.BillingItem;
import de.phsoftware.textManager.entities.Customer;

/**
 * JTable adapted to match the needs which arise if BillingItems need to be
 * displayed and interacted with
 * 
 * @author philnate
 * 
 */
public class BillingItemTable extends JTable {
    private static final long serialVersionUID = 2641781237435176875L;

    private final BillingItemTableModel model;

    public BillingItemTable(final JFrame frame) {
	super();
	setRowHeight(24);
	// set some default Renderer and Editor to properly display a list of
	// Documents
	DocumentListRenderer docList = new DocumentListRenderer(frame);
	setDefaultRenderer(List.class, docList);
	setDefaultEditor(List.class, docList);

	// set model and adjust column Model
	model = new BillingItemTableModel(getCaptions("mw.tableheader",
		"title", "wc", "cw", "fixPrice", "total", "documents"));
	setModel(model);
	getColumnModel().getColumn(0).setPreferredWidth(400);
	getColumnModel().getColumn(5).setPreferredWidth(400);
	setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	// Menu for Row actions, e.g. delete row
	addMouseListener(new MouseAdapter() {
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
				int row = rowAtPoint(e.getPoint());
				((BillingItem) ((Vector) model.getDataVector()
					.get(row)).get(0)).delete();
				model.removeRow(row);
			    }
			}
		    });
		    menu.add(delete);
		    menu.show(BillingItemTable.this, e.getX(), e.getY());
		}
	    }
	});
    }

    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
	if (4 == colIndex) {
	    return (Boolean) dataModel.getValueAt(rowIndex, 3);
	} else {
	    return true;
	}
    }

    /**
     * Creates a new BillingItem and returns it. BillingItem is initialized with
     * current selected customer, month and year.
     * 
     * @return BillingItem newly created BillingItem with basic Initialization
     */
    public BillingItem addNewBillingItem(Customer customer, int month, int year) {
	BillingItem item = new BillingItem().setCustomerId(customer.getId())
		.setMonth(month).setYear(year);
	model.addRow(new Object[] { item });
	item.save();
	return item;
    }

    /**
     * removes all currently set rows within the table
     */
    public void flushRows() {
	model.setRowCount(0);
    }

    public void addRow(BillingItem item) {
	model.addRow(new Object[] { item });
    }
}
