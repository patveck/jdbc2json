package com.pascalvaneck.jdbc2json.db;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface DbVisitor {

    void visitTable(@Nonnull final String s, @Nonnull final List<String> keyColumnNames);

    void visitRow(@Nonnull final Map<String, Object> row);

    void close();

}
