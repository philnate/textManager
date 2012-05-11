package de.phsoftware.textManager.updates;

import static de.phsoftware.textManager.utils.DB.ds;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import com.google.common.collect.Maps;

import de.phsoftware.textManager.entities.Setting;

public class Updater {
    private static final File installPath = getInstallPath();
    private static final int version = 1;
    private static Map<Integer, Class<? extends Update>> updates = Maps
	    .newTreeMap();

    /**
     * checks what the actual db version is, if an old version is encountered
     * appropriate updates are performed to get the db to the latest version
     */
    public static void checkUpdateNeeded() {
	createUpdateList();
	Setting v = Setting.findSetting("version");
	if (null == v) {
	    v = new Setting("version", "1");
	    ds.save(v);
	}
	backUp();
	rollback();
    }

    private static void createUpdateList() {
	updates.put(1, Update1.class);
    }

    /**
     * backups database in order some unexpected error occurs
     */
    public static void backUp() {
	ProcessBuilder dump = new ProcessBuilder(new File(installPath,
		"mongodump").toString(), "-h localhost", "--port 27017",
		"-o db.backUp");
	dump.directory(installPath);
	try {
	    dump.start();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * rolls back any made backups as something in the upgrade went wrong
     */
    private static void rollback() {
	// ProcessBuilder restore = new ProcessBuilder(new File(installPath,
	// "mongorestore").toString(), "--drop", "-h localhost",
	// "--port 27017", "-o db.backUp");
	// restore.directory(installPath);
	// try {
	// if (0==restore.start().exitValue()) {
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
    }

    private static File getInstallPath() {
	String path = Update.class.getProtectionDomain().getCodeSource()
		.getLocation().getPath();
	try {
	    String decodedPath = URLDecoder.decode(path, "UTF-8");
	    if (decodedPath.endsWith(".jar")) {
		return new File(new File(decodedPath).getParent(), "mongo");
	    } else {
		return new File("/usr/bin/");
	    }
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	    return null;
	}
    }
}
