package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.google.common.collect.Maps;

/**
 * simply key value document storing general settings about the app
 * 
 * @author user
 * 
 */
@Entity(noClassnameStored = true)
public class Setting extends Entry {

    @Id
    private String key;
    private String value;
    private static final Map<String, String> defaults = Maps.newHashMap();

    static {
	defaults.put("locale", Locale.getDefault().getLanguage());
    }

    public Setting() {
    }

    public Setting(String key, String value) {
	setKey(key);
	setValue(value);
    }

    public String getKey() {
	return key;
    }

    public Setting setKey(String key) {
	this.key = key;
	return this;
    }

    public String getValue() {
	return value;
    }

    public Setting setValue(String value) {
	this.value = value;
	return this;
    }

    public static Query<Setting> find() {
	return ds.find(Setting.class);
    }

    /**
     * returns the value for the given key or the default value if no such key
     * exists. In case no setting with this key can be found and the default
     * value will be returned the returned property won't be safed, this has to
     * happen manually.
     * 
     * @param key
     *            to look up
     * @param def
     *            default value for this key if no value is set
     * @return
     */
    public static Setting find(String key, String def) {
	Iterator<Setting> it = ds.find(Setting.class).filter("key =", key)
		.limit(1).fetch().iterator();
	if (it.hasNext()) {
	    return it.next();
	} else {
	    return new Setting(key, def);
	}
    }

    public static Setting find(String key) {
	return find(key, (defaults.containsKey(key)) ? defaults.get(key) : "");
    }
}
