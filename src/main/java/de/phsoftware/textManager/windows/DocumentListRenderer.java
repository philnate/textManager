package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.phsoftware.textManager.entities.Document;
import de.phsoftware.textManager.utils.ImageRegistry;

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
		    selectedFile = ((Document) box.getSelectedItem())
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
