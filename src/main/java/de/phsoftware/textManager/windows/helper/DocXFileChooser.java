package de.phsoftware.textManager.windows.helper;

import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import de.phsoftware.textManager.entities.Setting;

public class DocXFileChooser extends JFileChooser {
    private static String lastDir = "lastDocXDirectory";
    /**
     * 
     */
    private static final long serialVersionUID = -660825403864448172L;

    public DocXFileChooser() {
	setAcceptAllFileFilterUsed(false);
	setMultiSelectionEnabled(true);
	setCurrentDirectory(new File(Setting.find(lastDir,
		getCurrentDirectory().toString()).getValue()));
	setFileSelectionMode(JFileChooser.FILES_ONLY);
	addChoosableFileFilter(new FileFilter() {

	    @Override
	    public String getDescription() {
		return getCaption("fileFilter.docX");
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
    }

    @Override
    public int showOpenDialog(Component parent) throws HeadlessException {
	int returnCode = super.showOpenDialog(parent);
	new Setting(lastDir, getCurrentDirectory().toString()).save();
	return returnCode;
    }
}
