package de.phsoftware.textManager.utils;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18N {
    static ResourceBundle captions = ResourceBundle.getBundle("captions",
	    new Locale("de", "DE"));

    public static String getCaption(String key) {
	try {
	    return captions.getString(key);
	} catch (MissingResourceException e) {
	    return key;
	}

    }

    public static String[] getCaptions(String prefix, String... suffixes) {
	String[] caption = new String[suffixes.length];
	for (int i = 0; i < suffixes.length; i++) {
	    caption[i] = getCaption(prefix + "." + suffixes[i]);
	}
	return caption;
    }
}
