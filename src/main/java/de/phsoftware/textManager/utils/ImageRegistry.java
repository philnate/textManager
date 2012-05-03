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
