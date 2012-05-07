package de.phsoftware.textManager.windows;
import static de.phsoftware.textManager.utils.I18N.getCaption;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class DocXFileChooser extends JFileChooser {

    /**
     * 
     */
    private static final long serialVersionUID = -660825403864448172L;

    public DocXFileChooser() {
	setAcceptAllFileFilterUsed(false);
	setMultiSelectionEnabled(true);
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
}
