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
package me.philnate.textmanager.windows.components;

import static me.philnate.textmanager.utils.I18N.getCaption;
import static me.philnate.textmanager.utils.I18N.getCaptions;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.entities.Customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

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

    private boolean contextMenuEnabled = true;
    private static Logger LOG = LoggerFactory.getLogger(BillingItemTable.class);

    public BillingItemTable(final Container container, boolean showDocsField) {
	super();
	setRowHeight(24);
	// set model and adjust column Model
	if (showDocsField) {
	    model = new BillingItemTableModel(getCaptions("bit.header",
		    "title", "wc", "cw", "fixPrice", "total", "documents"));
	} else {
	    model = new BillingItemTableModel(getCaptions("bit.header",
		    "title", "wc", "cw", "fixPrice", "total"));
	}
	setModel(model);

	// set some default Renderer and Editor to properly display a list of
	// Documents
	if (showDocsField) {
	    DocumentListRenderer docList = new DocumentListRenderer(container);
	    setDefaultRenderer(List.class, docList);
	    setDefaultEditor(List.class, docList);
	    getColumnModel().getColumn(5).setPreferredWidth(400);
	}
	getColumnModel().getColumn(0).setPreferredWidth(400);
	setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	// Menu for Row actions, e.g. delete row
	addMouseListener(new MouseAdapter() {
	    @Override
	    public void mousePressed(MouseEvent e) {
		if (contextMenuEnabled && e.getButton() == MouseEvent.BUTTON3) {
		    JPopupMenu menu = new JPopupMenu();
		    JMenuItem delete = new JMenuItem(
			    getCaption("mw.itemmenu.delete"));
		    delete.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
			    if (JOptionPane.showConfirmDialog(container,
				    getCaption("mw.dialog.itemdelete.title")) == JOptionPane.YES_OPTION) {
				int[] rows = getSelectedRows();
				// we need to sort the rows descending, else we
				// could run into AIOOBE or remove the wrong
				// items
				Collections.reverse(Ints.asList(rows));
				LOG.debug("Going to remove rows: "
					+ Arrays.toString(rows));
				for (int row : rows) {
				    ((BillingItem) ((Vector) model
					    .getDataVector().get(row)).get(0))
					    .delete();
				    model.removeRow(row);
				}
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

    public void addRows(List<BillingItem> items) {
	for (BillingItem item : items) {
	    addRow(item);
	}
    }

    /**
     * per default this menu is activated
     * 
     * @param enabled
     */
    public void setContextMenuEnabled(boolean enabled) {
	contextMenuEnabled = enabled;
    }
}
