package com.pascalvaneck.mysql2json.export;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonExporterTest {

    private static final Log LOG = LogFactory.getLog(JsonExporterTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testVisitTwoRowsNoKey() throws IOException {
        try (Writer sw = new StringWriter()) {
            JsonExporter je = new JsonExporter(sw, Collections.emptyList());
            Map<String, Object> given = new HashMap<>();
            given.put("key2", 1);
            given.put("name", "name1");
            je.visitRow(given);
            LOG.debug("First row done. Got so far: " + sw.toString());
            given = new HashMap<>();
            given.put("key2", 2);
            given.put("name", "name2");
            je.visitRow(given);
            LOG.debug("Second row done. Got so far: " + sw.toString());
            je.close();
            LOG.debug("All done. Got: " + sw.toString());
            String expected = "[{\"key2\":1,\"name\":\"name1\"},{\"key2\":2,\"name\":\"name2\"}]";
            assertEquals("Result must match expected JSON", expected, sw.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testVisitOneRowOneKey() throws IOException {
        try (Writer sw = new StringWriter()) {
            JsonExporter je = new JsonExporter(sw, Collections.singletonList("key2"));
            Map<String, Object> given = new HashMap<>();
            given.put("key2", 1);
            given.put("name", "name1");
            je.visitRow(given);
            LOG.debug("First row done. Got so far: " + sw.toString());
            je.close();
            LOG.debug("All done. Got: " + sw.toString());
            String expected = "{\"key2\":{\"1\":{\"name\":\"name1\"}}}";
            assertEquals("Result must match expected JSON", expected, sw.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testVisitTwoRowsOneKey() throws IOException {
        try (Writer sw = new StringWriter()) {
            JsonExporter je = new JsonExporter(sw, Collections.singletonList("key2"));
            Map<String, Object> given = new HashMap<>();
            given.put("key2", 1);
            given.put("name", "name1");
            je.visitRow(given);
            LOG.info("First row done. Got so far: " + sw.toString());
            given = new HashMap<>();
            given.put("key2", 2);
            given.put("name", "name2");
            je.visitRow(given);
            LOG.info("Second row done. Got so far: " + sw.toString());
            je.close();
            LOG.info("All done. Got: " + sw.toString());
            String expected = "{\"key2\":{\"1\":{\"name\":\"name1\"},\"2\":{\"name\":\"name2\"}}}";
            assertEquals("Result must match expected JSON", expected, sw.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testVisitFourRowsTwoKeys() throws IOException {
        try (Writer sw = new StringWriter()) {
            JsonExporter je = new JsonExporter(sw, new ArrayList<>(Arrays.asList("key1", "key2")));
            Map<String, Object> given = new HashMap<>();
            given.put("key1", "a");
            given.put("key2", 1);
            given.put("name", "name1");
            je.visitRow(given);
            LOG.info("First row done. Got so far: " + sw.toString());
            given = new HashMap<>();
            given.put("key1", "a");
            given.put("key2", 2);
            given.put("name", "name2");
            je.visitRow(given);
            LOG.info("Second row done. Got so far: " + sw.toString());
            given = new HashMap<>();
            given.put("key1", "b");
            given.put("key2", 1);
            given.put("name", "name3");
            je.visitRow(given);
            LOG.info("Third row done. Got so far: " + sw.toString());
            given = new HashMap<>();
            given.put("key1", "b");
            given.put("key2", 2);
            given.put("name", "name4");
            je.visitRow(given);
            LOG.info("Fourth row done. Got so far: " + sw.toString());
            je.close();
            LOG.info("All done. Got: " + sw.toString());
            String expected = "{\"key1\":{\"a\":{\"key2\":{\"1\":{\"name\":\"name1\"},\"2\":{\"name\":\"name2\"}}},\"b\":{\"key2\":{\"1\":{\"name\":\"name3\"},\"2\":{\"name\":\"name4\"}}}}}";
            assertEquals("Result must match expected JSON", expected, sw.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Test
    public void testVisitTwoRowsTwoKeys() throws IOException {
        try (Writer sw = new StringWriter()) {
            JsonExporter je = new JsonExporter(sw, new ArrayList<>(Arrays.asList("key1", "key2")));
            Map<String, Object> given = new HashMap<>();
            given.put("key1", "a");
            given.put("key2", 1);
            given.put("name", "name1");
            je.visitRow(given);
            LOG.info("First row done. Got so far: " + sw.toString());
            given = new HashMap<>();
            given.put("key1", "b");
            given.put("key2", 1);
            given.put("name", "name3");
            je.visitRow(given);
            LOG.info("Second row done. Got so far: " + sw.toString());
            je.close();
            LOG.info("All done. Got: " + sw.toString());
            String expected = "{\"key1\":{\"a\":{\"key2\":{\"1\":{\"name\":\"name1\"}}},\"b\":{\"key2\":{\"1\":{\"name\":\"name3\"}}}}}";
            assertEquals("Result must match expected JSON", expected, sw.toString());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
