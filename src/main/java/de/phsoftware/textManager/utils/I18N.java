package de.phsoftware.textManager.utils;

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

public class I18N {
    static ResourceBundle captions = ResourceBundle

    .getBundle("captions", new Locale("de", "DE"), new UTF8Control());

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
}
