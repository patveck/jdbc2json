package com.pascalvaneck.mysql2json.util;

import com.coveo.nashorn_modules.Require;
import com.coveo.nashorn_modules.ResourceFolder;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSTests {

    private NashornScriptEngine engine = null;
    private CachedFolder rootFolder = null;

    @Before
    public void setUpJSEngine() {
        engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
        ResourceFolder backingRootFolder = ResourceFolder.create(getClass().getClassLoader(), "com/pascalvaneck/es5", "UTF-8");
        rootFolder = CachedFolder.create("/", backingRootFolder);
        try {
            Require.enable(engine, rootFolder);
            engine.eval("print('Nashorn Javascript engine running.');");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void isJSEngineRunning() {
        rootFolder.setFile("testmod.js", "module.exports = '{ \"snapshotId\": { \"1\": { \"nodeId\": { \"2\": { \"key1\": 3 }}}}}';");
        try {
            engine.eval("var my = require('testmod.js'); var v=JSON.parse(my); print(v); print('Value is:' + v.snapshotId[\"1\"].nodeId[\"2\"].key1 +'.');");
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }
}
