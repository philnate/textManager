package de.phsoftware.textManager.entities;

import static de.phsoftware.textManager.utils.DB.ds;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Per convention classes implementing Entry should implement a
 * <code>public static Query&lt;T&gt; find()</code> method as well as a find
 * method with parameters of the primary doc key returning at most one document
 * ( <code>public static
 * T find(Object... docKeyPart)</code>)
 * 
 * @author philnate
 * 
 */
public abstract class Entry {

    public void save() {
	ds.save(this);
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }

    /**
     * deletes the current entry and all linked documents if existent
     */
    public abstract void delete();
}
