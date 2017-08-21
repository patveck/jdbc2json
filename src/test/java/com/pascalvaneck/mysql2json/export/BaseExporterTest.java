package com.pascalvaneck.mysql2json.export;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class BaseExporterTest {

    private static final Log LOG = LogFactory.getLog(BaseExporterTest.class);

    private FileSystem fs;
    private Path root;

    @Before
    public void setUp() throws Exception {
        fs = Jimfs.newFileSystem(Configuration.unix());
        root = fs.getPath("/foo");
        Files.createDirectory(root);
    }

    @After
    public void tearDown() throws Exception {
        fs.close();
    }

    @Test
    public void visitDb() throws Exception {
        BaseExporter be = new BaseExporter(root) {
            @Override
            public void visitRow(@Nonnull final Map<String, Object> row) {
                LOG.info(row.toString());
            }
            public void close() {}
        };
        be.visitTable("bar");
        assertTrue("Path /foo/bar should exist", Files.exists(fs.getPath("/foo/bar")));
    }

}
