package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class Entry {

    public void save() {
	ds.save(this);
    }

    // public abstract Entry load(Object id);

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }
}
