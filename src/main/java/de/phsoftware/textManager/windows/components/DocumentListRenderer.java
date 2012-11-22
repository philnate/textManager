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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.phsoftware.textManager.entities.Document;
import de.phsoftware.textManager.utils.ImageRegistry;
import de.phsoftware.textManager.windows.helper.DocXFileChooser;

public class DocumentListRenderer implements TableCellRenderer, TableCellEditor {

    CellEditorListener listener = null;
    JFrame parentWindow;
    // MainWindow window;
    private Object selectedFile;

    public DocumentListRenderer(JFrame parentWindow/* , MainWindow window */) {
	this.parentWindow = parentWindow;
	// this.window = window;
    }

    public void addCellEditorListener(CellEditorListener arg0) {
	if (null == listener) {
	    listener = arg0;
	} else {
	    throw new IllegalStateException("Already listener set");
	}
    }

    public void cancelCellEditing() {
	// TODO Auto-generated method stub

    }

    public Object getCellEditorValue() {
	// TODO Auto-generated method stub
	return selectedFile;
    }

    public boolean isCellEditable(EventObject arg0) {
	// TODO Auto-generated method stub
	return true;
    }

    public void removeCellEditorListener(CellEditorListener arg0) {
	listener = null;
    }

    public boolean shouldSelectCell(EventObject arg0) {
	// TODO Auto-generated method stub
	return false;
    }

    public boolean stopCellEditing() {
	// TODO Auto-generated method stub
	return false;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	return getTableCell(value);
    }

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

	    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
	    }

	    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		that.fireEditingStopped();
	    }

	    public void popupMenuCanceled(PopupMenuEvent e) {
		// TODO Auto-generated method stub
	    }
	});

	panel.add(attachedDocs, "w 320!, h ::100%");
	JButton upload = new JButton();
	upload.setIcon(ImageRegistry.getImage("load.gif"));
	upload.setToolTipText(getCaption("mw.tooltip.cell.add"));

	upload.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JFileChooser file = new DocXFileChooser();
		switch (file.showOpenDialog(parentWindow)) {
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

	    public void actionPerformed(ActionEvent e) {
		switch (JOptionPane
			.showConfirmDialog(
				null,
				"Das Dokument wird aus dem System gelöscht, wenn sie keine Kopie besitzen wird das Dokument unwiderruflich gelöscht.\nSoll dieses Dokument wirklich gelöscht werden?",
				"Dokument löschen", JOptionPane.YES_NO_OPTION)) {
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
