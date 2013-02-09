/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2013 philnate
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

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import me.philnate.textmanager.entities.BillingItem;
import me.philnate.textmanager.entities.Document;

import org.bson.types.ObjectId;

/**
 * Table model used to display BillingItems
 * 
 * @author philnate
 * 
 */
class BillingItemTableModel extends DefaultTableModel {

    private static final long serialVersionUID = -1178926028075689166L;

    public BillingItemTableModel(String[] columnNames) {
	super(columnNames, 0);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
	switch (columnIndex) {
	case 0:
	    return String.class;
	case 1:
	    return Integer.class;
	case 3:
	    return Boolean.class;
	case 2: /* fallthrough */
	case 4:
	    return Double.class;
	case 5:
	    return List.class;
	default:
	    throw new IllegalAccessError("Got columnIndex out of bounds");
	}
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
	@SuppressWarnings("rawtypes")
	BillingItem item = (BillingItem) ((Vector) dataVector
		.elementAt(rowIndex)).get(0);
	switch (columnIndex) {
	case 0:
	    return item.getTitle();
	case 1:
	    return item.getWordCount();
	case 2:
	    return item.getCentPerWord();
	case 3:
	    return item.isFixedPrice();
	case 4:
	    return item.getTotal();
	case 5:
	    return item.getDocuments();
	default:
	    throw new IllegalArgumentException("Got columnIndex out of bounds");
	}
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
	@SuppressWarnings("rawtypes")
	BillingItem item = (BillingItem) ((Vector) dataVector
		.elementAt(rowIndex)).get(0);
	switch (columnIndex) {
	case 0:
	    item.setTitle((String) value);
	    break;
	case 1:
	    item.setWordCount((Integer) value);
	    if (!item.isFixedPrice()) {
		setValueAt(item.getWordCount() * item.getCentPerWord(),
			rowIndex, 4);
		fireTableCellUpdated(rowIndex, 4);
		return;// no need to save twice
	    }
	    break;
	case 2:
	    item.setCentPerWord((Double) value);
	    if (!item.isFixedPrice()) {
		setValueAt(item.getWordCount() * item.getCentPerWord(),
			rowIndex, 4);
		fireTableCellUpdated(rowIndex, 4);
		return;// no need to save twice
	    }
	    break;
	case 3:
	    item.setFixedPrice((Boolean) value);
	    break;
	case 4:
	    item.setTotal((Double) value);
	    break;
	case 5:
	    if (null != value && value.getClass().isArray()) {
		for (File file : (File[]) value) {
		    item.addDocument(Document.loadAndSave(file));
		}
	    } else if (value instanceof ObjectId) {
		item.removeDocument((ObjectId) value);
	    }
	    break;
	default:
	    throw new IllegalArgumentException("Got columnIndex out of bounds");
	}
	item.save();
    }

    public void addRow(BillingItem item) {
	super.addRow(new Object[] { item });
    }

    /**
     * use {@link BillingItemTableModel#addRow(BillingItem)} instead. Throws
     * exception
     */
    @Override
    @Deprecated
    public void addRow(Object[] rowData) {
	throw new IllegalAccessError("This method isn't supposed to be used");
    }

    /**
     * use {@link BillingItemTableModel#addRow(BillingItem)} instead.
     */
    @Override
    @Deprecated
    public void addRow(Vector rowData) {
	super.addRow(rowData);
    }
}
