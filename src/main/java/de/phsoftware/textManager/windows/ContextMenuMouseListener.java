package de.phsoftware.textManager.windows;

import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

/**
 * Right Click, copy&paste menu copied from
 * http://stackoverflow.com/questions/2793940
 * /why-right-click-is-not-working-on-java-application
 * 
 */
public class ContextMenuMouseListener extends MouseAdapter {
    private final JPopupMenu popup = new JPopupMenu();

    private final Action cutAction;
    private final Action copyAction;
    private final Action pasteAction;
    private final Action undoAction;
    private final Action selectAllAction;

    private JTextComponent textComponent;
    private String savedString = "";
    private Actions lastActionSelected;

    public static final ContextMenuMouseListener RIGHT_CLICK_MENU = new ContextMenuMouseListener();

    private enum Actions {
	UNDO, CUT, COPY, PASTE, SELECT_ALL
    };

    @SuppressWarnings("serial")
    private ContextMenuMouseListener() {
	undoAction = new AbstractAction(getCaption("menu.rc.text.undo")) {

	    public void actionPerformed(ActionEvent ae) {
		textComponent.setText("");
		textComponent.replaceSelection(savedString);

		lastActionSelected = Actions.UNDO;
	    }
	};

	popup.add(undoAction);
	popup.addSeparator();

	cutAction = new AbstractAction(getCaption("menu.rc.text.cut")) {

	    public void actionPerformed(ActionEvent ae) {
		lastActionSelected = Actions.CUT;
		savedString = textComponent.getText();
		textComponent.cut();
	    }
	};

	popup.add(cutAction);

	copyAction = new AbstractAction(getCaption("menu.rc.text.copy")) {

	    public void actionPerformed(ActionEvent ae) {
		lastActionSelected = Actions.COPY;
		textComponent.copy();
	    }
	};

	popup.add(copyAction);

	pasteAction = new AbstractAction(getCaption("menu.rc.text.paste")) {

	    public void actionPerformed(ActionEvent ae) {
		lastActionSelected = Actions.PASTE;
		savedString = textComponent.getText();
		textComponent.paste();
	    }
	};

	popup.add(pasteAction);
	popup.addSeparator();

	selectAllAction = new AbstractAction(
		getCaption("menu.rc.text.selectAll")) {

	    public void actionPerformed(ActionEvent ae) {
		lastActionSelected = Actions.SELECT_ALL;
		textComponent.selectAll();
	    }
	};

	popup.add(selectAllAction);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
	    if (!(e.getSource() instanceof JTextComponent)) {
		return;
	    }

	    textComponent = (JTextComponent) e.getSource();
	    textComponent.requestFocus();

	    boolean enabled = textComponent.isEnabled();
	    boolean editable = textComponent.isEditable();

	    // don't display context menu if Box is disabled or not editable
	    if (!enabled || !editable) {
		return;
	    }

	    boolean nonempty = !(textComponent.getText() == null || textComponent
		    .getText().equals(""));
	    boolean marked = textComponent.getSelectedText() != null;

	    boolean pasteAvailable = Toolkit.getDefaultToolkit()
		    .getSystemClipboard().getContents(null)
		    .isDataFlavorSupported(DataFlavor.stringFlavor);

	    undoAction
		    .setEnabled(enabled
			    && editable
			    && (lastActionSelected == Actions.CUT || lastActionSelected == Actions.PASTE));
	    cutAction.setEnabled(enabled && editable && marked);
	    copyAction.setEnabled(enabled && marked);
	    pasteAction.setEnabled(enabled && editable && pasteAvailable);
	    selectAllAction.setEnabled(enabled && nonempty);

	    int nx = e.getX();

	    if (nx > 500) {
		nx = nx - popup.getSize().width;
	    }

	    popup.show(e.getComponent(), nx, e.getY() - popup.getSize().height);
	}
    }
}
