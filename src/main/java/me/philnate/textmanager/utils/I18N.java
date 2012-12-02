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
package me.philnate.textmanager.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import me.philnate.textmanager.entities.Setting;


public class I18N {

    static ResourceBundle captions;
    static final Locale locale;
    static {
	locale = new Locale(Setting.find("locale", "de").getValue());
	captions = ResourceBundle.getBundle("captions", locale,
		new UTF8Control());
    }

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

    /**
     * Copied from http://stackoverflow.com/a/4660195
     * 
     * @author <a href="http://stackoverflow.com/users/157882/balusc">balusc</a>
     * 
     */
    private static class UTF8Control extends Control {
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale,
		String format, ClassLoader loader, boolean reload)
		throws IllegalAccessException, InstantiationException,
		IOException {
	    // The below is a copy of the default implementation.
	    String bundleName = toBundleName(baseName, locale);
	    String resourceName = toResourceName(bundleName, "properties");
	    ResourceBundle bundle = null;
	    InputStream stream = null;
	    if (reload) {
		URL url = loader.getResource(resourceName);
		if (url != null) {
		    URLConnection connection = url.openConnection();
		    if (connection != null) {
			connection.setUseCaches(false);
			stream = connection.getInputStream();
		    }
		}
	    } else {
		stream = loader.getResourceAsStream(resourceName);
	    }
	    if (stream != null) {
		try {
		    // Only this line is changed to make it to read properties
		    // files as UTF-8.
		    bundle = new PropertyResourceBundle(new InputStreamReader(
			    stream, "UTF-8"));
		} finally {
		    stream.close();
		}
	    }
	    return bundle;
	}
    }

    /**
     * returns the locale found within the settings, this locale is used
     * throughout the system to prepare the presentation of floats as well as
     * the language used for Labels
     * 
     * @return
     */
    public static Locale getLocale() {
	return locale;
    }
}
