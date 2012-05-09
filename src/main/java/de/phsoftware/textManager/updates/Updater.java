package de.phsoftware.textManager.updates;

import java.util.Map;

import com.google.common.collect.Maps;

public class Updater {
    private static final int version = 1;
    private static Map<Integer, Class<? extends Update>> updates = Maps
	    .newLinkedHashMap();

    /**
     * checks what the actual db version is, if an old version is encountered
     * appropriate updates are performed to get the db to the latest version
     */
    public static void checkUpdateNeeded() {
    }
}
