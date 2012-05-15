package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

public abstract class Entry {

    public void save() {
	ds.save(this);
    }

    // public abstract Entry load(Object id);
}
