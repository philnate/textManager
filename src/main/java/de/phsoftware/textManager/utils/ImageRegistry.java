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
package de.phsoftware.textManager.utils;

import java.util.Map;

import javax.swing.ImageIcon;

import com.google.common.collect.Maps;

public class ImageRegistry {

    private static Map<String, ImageIcon> icons = Maps.newHashMap();

    public static ImageIcon getImage(String name) {
	if (!icons.containsKey(name)) {
	    icons.put(name, new ImageIcon(ImageRegistry.class.getClassLoader()
		    .getResource(name)));
	}
	return icons.get(name);
    }
}
