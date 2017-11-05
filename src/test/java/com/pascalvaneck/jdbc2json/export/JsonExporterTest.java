package com.pascalvaneck.jdbc2json.export;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonExporterTest {

    private static final Log LOG = LogFactory.getLog(JsonExporterTest.class);

    private FileSystem fs;
    private Path testOutput;

    @Before
    public void setUp() throws Exception {
        fs = Jimfs.newFileSystem(Configuration.unix());
        testOutput = fs.getPath("/foo.json");
        Files.createFile(testOutput);
    }

    @After
    public void tearDown() throws Exception {
        fs.close();
    }

    private String fileToString(@Nonnull final Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    @Test
    public void testVisitTwoRowsNoKey() throws IOException {
        JsonExporter je = new JsonExporter(testOutput, Syntax.COMMONJS, Collections.emptyList());
        Map<String, Object> given = new HashMap<>();
        given.put("key2", 1);
        given.put("name", "name1");
        je.visitRow(given);
        LOG.debug("First row done. Got so far: " + fileToString(testOutput));
        given = new HashMap<>();
        given.put("key2", 2);
        given.put("name", "name2");
        je.visitRow(given);
        LOG.debug("Second row done. Got so far: " + fileToString(testOutput));
        je.close();
        LOG.debug("All done. Got: " + fileToString(testOutput));
        String expected = "[{\"key2\":1,\"name\":\"name1\"},{\"key2\":2,\"name\":\"name2\"}]";
        assertEquals("Result must match expected JSON", expected, fileToString(testOutput));
    }

    @Test
    public void testVisitOneRowOneKey() throws IOException {
        JsonExporter je = new JsonExporter(testOutput, Syntax.COMMONJS, Collections.singletonList("key2"));
        Map<String, Object> given = new HashMap<>();
        given.put("key2", 1);
        given.put("name", "name1");
        je.visitRow(given);
        LOG.debug("First row done. Got so far: " + fileToString(testOutput));
        je.close();
        LOG.debug("All done. Got: " + fileToString(testOutput));
        String expected = "{\"key2\":{\"1\":{\"name\":\"name1\"}}}";
        assertEquals("Result must match expected JSON", expected, fileToString(testOutput));
    }

    @Test
    public void testVisitTwoRowsOneKey() throws IOException {
        JsonExporter je = new JsonExporter(testOutput, Syntax.COMMONJS, Collections.singletonList("key2"));
        Map<String, Object> given = new HashMap<>();
        given.put("key2", 1);
        given.put("name", "name1");
        je.visitRow(given);
        LOG.info("First row done. Got so far: " + fileToString(testOutput));
        given = new HashMap<>();
        given.put("key2", 2);
        given.put("name", "name2");
        je.visitRow(given);
        LOG.info("Second row done. Got so far: " + fileToString(testOutput));
        je.close();
        LOG.info("All done. Got: " + fileToString(testOutput));
        String expected = "{\"key2\":{\"1\":{\"name\":\"name1\"},\"2\":{\"name\":\"name2\"}}}";
        assertEquals("Result must match expected JSON", expected, fileToString(testOutput));
    }

    @Test
    public void testVisitFourRowsTwoKeys() throws IOException {
        JsonExporter je = new JsonExporter(testOutput, Syntax.COMMONJS, new ArrayList<>(Arrays.asList("key1", "key2")));
        Map<String, Object> given = new HashMap<>();
        given.put("key1", "a");
        given.put("key2", 1);
        given.put("name", "name1");
        je.visitRow(given);
        LOG.info("First row done. Got so far: " + fileToString(testOutput));
        given = new HashMap<>();
        given.put("key1", "a");
        given.put("key2", 2);
        given.put("name", "name2");
        je.visitRow(given);
        LOG.info("Second row done. Got so far: " + fileToString(testOutput));
        given = new HashMap<>();
        given.put("key1", "b");
        given.put("key2", 1);
        given.put("name", "name3");
        je.visitRow(given);
        LOG.info("Third row done. Got so far: " + fileToString(testOutput));
        given = new HashMap<>();
        given.put("key1", "b");
        given.put("key2", 2);
        given.put("name", "name4");
        je.visitRow(given);
        LOG.info("Fourth row done. Got so far: " + fileToString(testOutput));
        je.close();
        LOG.info("All done. Got: " + fileToString(testOutput));
        String expected = "{\"key1\":{\"a\":{\"key2\":{\"1\":{\"name\":\"name1\"},\"2\":{\"name\":\"name2\"}}},\"b\":{\"key2\":{\"1\":{\"name\":\"name3\"},\"2\":{\"name\":\"name4\"}}}}}";
        assertEquals("Result must match expected JSON", expected, fileToString(testOutput));
    }

    @Test
    public void testVisitTwoRowsTwoKeys() throws IOException {
        JsonExporter je = new JsonExporter(testOutput, Syntax.COMMONJS, new ArrayList<>(Arrays.asList("key1", "key2")));
        Map<String, Object> given = new HashMap<>();
        given.put("key1", "a");
        given.put("key2", 1);
        given.put("name", "name1");
        je.visitRow(given);
        LOG.info("First row done. Got so far: " + fileToString(testOutput));
        given = new HashMap<>();
        given.put("key1", "b");
        given.put("key2", 1);
        given.put("name", "name3");
        je.visitRow(given);
        LOG.info("Second row done. Got so far: " + fileToString(testOutput));
        je.close();
        LOG.info("All done. Got: " + fileToString(testOutput));
        String expected = "{\"key1\":{\"a\":{\"key2\":{\"1\":{\"name\":\"name1\"}}},\"b\":{\"key2\":{\"1\":{\"name\":\"name3\"}}}}}";
        assertEquals("Result must match expected JSON", expected, fileToString(testOutput));
    }

}
