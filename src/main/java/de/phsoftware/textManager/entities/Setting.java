package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import java.util.Iterator;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

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

    public static Setting findSetting(String key) {
	Iterator<Setting> it = ds.find(Setting.class).filter("key =", key)
		.limit(1).fetch().iterator();
	if (it.hasNext()) {
	    return it.next();
	} else {
	    return new Setting(key, "");
	}
    }
}
