package de.phsoftware.textManager.windows;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.phsoftware.textManager.entities.Document;

public class DocumentListRenderer implements TableCellRenderer, TableCellEditor {

    CellEditorListener listener = null;
    JFrame parentWindow;
    private Object selectedFile;

    public DocumentListRenderer(JFrame parentWindow) {
	this.parentWindow = parentWindow;
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

    private Component getTableCell(Object value) {
	final DocumentListRenderer that = this;
	JPanel panel = new JPanel(new MigLayout("insets 0, gap 0! 0!",
		"[][][]", ""));// new GridLayout(1, 3, 0, 0));
	panel.setBorder(BorderFactory.createEmptyBorder());
	final JComboBox box = new JComboBox();

	box.addMouseListener(new MouseAdapter() {

	    @Override
	    public void mouseReleased(MouseEvent arg0) {
		that.fireEditingStopped();
	    }
	});

	panel.add(box, "w 320!, h ::100%");
	JButton upload = new JButton("+");

	upload.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JFileChooser file = new JFileChooser();
		file.setAcceptAllFileFilterUsed(false);
		file.setMultiSelectionEnabled(true);
		file.setFileSelectionMode(JFileChooser.FILES_ONLY);
		file.addChoosableFileFilter(new FileFilter() {

		    @Override
		    public String getDescription() {
			return "Word .docx Datei";
		    }

		    @Override
		    public boolean accept(File arg0) {
			if (arg0.getName().endsWith(".docx")
				|| arg0.getName().endsWith(".doc")
				|| arg0.isDirectory()) {
			    return true;
			}
			return false;
		    }
		});
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
	JButton remove = new JButton("x");
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
		    selectedFile = ((Document) box.getSelectedItem())
			    .getDocument();
		    break;
		}
		that.fireEditingStopped();
		selectedFile = null;
	    }
	});
	panel.add(remove);// ,"w 40!, h ::100%");

	@SuppressWarnings("unchecked")
	List<Document> docs = (List<Document>) value;
	if (null != docs && 0 != docs.size()) {
	    remove.setEnabled(true);
	    for (Document doc : docs) {
		box.addItem(doc);
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
