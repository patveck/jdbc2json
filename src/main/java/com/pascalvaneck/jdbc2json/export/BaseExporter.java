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

    protected List<String> keyColumnNames;

    public BaseExporter(@Nonnull final Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void visitTable(@Nonnull String tableName, @Nonnull List<String> keyColumnNames) {
        this.keyColumnNames = keyColumnNames;
        final Path newPath = outputPath.resolve(tableName);
        try {
            Files.createDirectory(newPath);
        } catch (IOException e) {
            LOG.fatal(e.getMessage(), e);
        }
    }
}
