/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) ${year} philnate
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
package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.query.Query;
import com.google.common.collect.Maps;
import com.mongodb.WriteConcern;

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
	List<Setting> list = ds.find(Setting.class).filter("key =", key)
		.limit(1).asList();
	if (list.size() == 1) {
	    return list.get(0);
	} else {
	    return new Setting(key, def);
	}
    }

    public static Setting find(String key) {
	return find(key, (defaults.containsKey(key)) ? defaults.get(key) : "");
    }

    @Override
    public void delete() {
	ds.delete(this, WriteConcern.SAFE);
    }
}
