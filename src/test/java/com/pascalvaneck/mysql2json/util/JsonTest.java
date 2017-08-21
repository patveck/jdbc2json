package com.pascalvaneck.mysql2json.util;

import com.fasterxml.jackson.jr.ob.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class JsonTest {

    private static final Log LOG = LogFactory.getLog(JsonTest.class);

    /*
    { "snapshotId": { "1": { "nodeId": { "1", { "key1": 2 }}}}}
     */

    @Test
    public void experiment() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("snapshotId", 1);
        map.put("name", "myName");
        try {
            LOG.info(JSON.std.asString(map));
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}
