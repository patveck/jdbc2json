package com.pascalvaneck.jdbc2json.export;

import com.pascalvaneck.jdbc2json.db.DbVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class BaseExporter implements DbVisitor {

    private static final Log LOG = LogFactory.getLog(BaseExporter.class);

    protected final Path outputPath;

    public BaseExporter(@Nonnull final Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void visitTable(@Nonnull String s) {
        LOG.info("Entering visitTable: " + s);
        final Path newPath = outputPath.resolve(s);
        try {
            Files.createDirectory(newPath);
        } catch (IOException e) {
            LOG.fatal(e.getMessage(), e);
        }
    }
}
