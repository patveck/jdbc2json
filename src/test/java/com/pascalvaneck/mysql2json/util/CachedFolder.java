package com.pascalvaneck.mysql2json.util;

import com.coveo.nashorn_modules.AbstractFolder;
import com.coveo.nashorn_modules.Folder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

public class CachedFolder extends AbstractFolder {

    private static final Log LOG = LogFactory.getLog(CachedFolder.class);

    private Folder backingFolder = null;

    private HashMap<String, String> memFiles = null;

    private CachedFolder(Folder parent, String path, Folder backingFolder) {
        super(parent, path);
        this.backingFolder = backingFolder;
        memFiles = new HashMap<>();
    }

    public static CachedFolder create(String path, Folder backingFolder) {
        return new CachedFolder(null, path, backingFolder);
    }

    public void setFile(String path, String file) {
        memFiles.put(path, file);
    }

    @Override
    public String getFile(String s) {
        if (memFiles.containsKey(s)) {
            LOG.info("Serving file " + s + " from memory.");
            return memFiles.get(s);
        } else {
            LOG.info("Serving file " + s + " from backing folder.");
            return getParent().getFile(s);
        }
    }

    @Override
    public Folder getFolder(String s) {
        return new CachedFolder(this, getPath() + s + "/", backingFolder.getFolder(s));
    }
}
