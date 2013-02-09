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

import static me.philnate.textmanager.utils.I18N.getCaption;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import me.philnate.textmanager.entities.Document;
import me.philnate.textmanager.utils.ImageRegistry;
import me.philnate.textmanager.windows.helper.DocXFileChooser;
import net.miginfocom.swing.MigLayout;

public class DocumentListRenderer implements TableCellRenderer, TableCellEditor {

    CellEditorListener listener = null;
    Container parent;
    private Object selectedFile;

    public DocumentListRenderer(Container parent) {
	this.parent = parent;
    }

    @Override
    public void addCellEditorListener(CellEditorListener arg0) {
	if (null == listener) {
	    listener = arg0;
	} else {
	    throw new IllegalStateException("Already listener set");
	}
    }

    @Override
    public void cancelCellEditing() {
	// TODO Auto-generated method stub

    }

    @Override
    public Object getCellEditorValue() {
	return selectedFile;
    }

    @Override
    public boolean isCellEditable(EventObject arg0) {
	// TODO Auto-generated method stub
	return true;
    }

    @Override
    public void removeCellEditorListener(CellEditorListener arg0) {
	listener = null;
    }

    @Override
    public boolean shouldSelectCell(EventObject arg0) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean stopCellEditing() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	return getTableCell(value);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
	    boolean isSelected, boolean hasFocus, int row, int column) {
	return getTableCell(value);
    }

    private Component getTableCell(final Object value) {
	final DocumentListRenderer that = this;
	JPanel panel = new JPanel(new MigLayout("insets 0, gap 0! 0!",
		"[][][][]", ""));// new GridLayout(1, 3, 0, 0));
	panel.setBorder(BorderFactory.createEmptyBorder());
	final JComboBox attachedDocs = new JComboBox();
	attachedDocs.addPopupMenuListener(new PopupMenuListener() {

	    @Override
	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
	    }

	    @Override
	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		that.fireEditingStopped();
	    }

	    @Override
	    public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub
	    }
	});

	panel.add(attachedDocs, "w 320!, h ::100%");
	JButton upload = new JButton();
	upload.setIcon(ImageRegistry.getImage("load.gif"));
	upload.setToolTipText(getCaption("mw.tooltip.cell.add"));

	upload.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		JFileChooser file = new DocXFileChooser();
		switch (file.showOpenDialog(parent)) {
		case JFileChooser.APPROVE_OPTION:
		    selectedFile = file.getSelectedFiles();
		    break;
		default:
		    selectedFile = null;
		    break;
		}
		that.fireEditingStopped();
		selectedFile = null;
	    }
	});
	panel.add(upload);// ,"w 40!, h ::100%");
	JButton remove = new JButton();
	remove.setIcon(ImageRegistry.getImage("remove.gif"));
	remove.setToolTipText(getCaption("mw.tooltip.cell.remove"));
	remove.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent e) {
		switch (JOptionPane.showConfirmDialog(null,
			getCaption("dlr.delete.msg"),
			getCaption("dlr.delete.title"),
			JOptionPane.YES_NO_OPTION)) {
		case JOptionPane.NO_OPTION:
		    selectedFile = null;
		    break;
		case JOptionPane.YES_OPTION:
		    selectedFile = ((Document) attachedDocs.getSelectedItem())
			    .getDocument();
		    break;
		}
		that.fireEditingStopped();
		selectedFile = null;
	    }
	});
	panel.add(remove);

	@SuppressWarnings("unchecked")
	List<Document> docs = (List<Document>) value;
	if (null != docs && 0 != docs.size()) {
	    remove.setEnabled(true);
	    for (Document doc : docs) {
		attachedDocs.addItem(doc);
	    }
	} else {
	    remove.setEnabled(false);
	}
	return panel;
    }

    private void fireEditingStopped() {
	ChangeEvent ce = new ChangeEvent(this);
	listener.editingStopped(ce);
    }
}
