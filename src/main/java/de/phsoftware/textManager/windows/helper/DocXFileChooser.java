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
