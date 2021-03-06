package com.pascalvaneck.jdbc2json.export;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonExporter extends BaseExporter {

    private static final Log LOG = LogFactory.getLog(JsonExporter.class);

    private final JsonFactory jf = new JsonFactory();

    private JsonGenerator jg;

    private Map<String, Object> previousRow;

    private final LinkedList<String> processedKeys = new LinkedList<>();

    private boolean arrayStarted;

    private Syntax syntax;

    private OutputStream stream;

    public JsonExporter(@Nonnull final Path path, @Nonnull final Syntax syntax) {
        super(path);
        this.syntax = syntax;
    }

    @Override
    public void close() {
        try {
            jg.flush();
            jg.close();
            stream.write(syntax.getPostfix().getBytes(Charset.forName("UTF-8")));
            stream.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void visitTable(@Nonnull String tableName, @Nonnull List<String> keyColumnNames) {
        super.visitTable(tableName, keyColumnNames);
        try {
            final Path path = outputPath.resolve(tableName + syntax.getExtension());
            Files.createFile(path);
            stream = Files.newOutputStream(path);
            stream.write(syntax.getPrefix(tableName).getBytes(Charset.forName("UTF-8")));
            stream.flush();
            jg = jf.createGenerator(stream).configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void visitRow(@Nonnull final Map<String, Object> row) {
        try {
            visitRow(row, 0);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                jg.flush();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void visitRow(@Nonnull Map<String, Object> row, int index) throws IOException {
        if (keyColumnNames.size() == index) {
            assert processedKeys.size() == index : "Processed keys stack has " + processedKeys.size() + " items while index is " + index;
            if (keyColumnNames.isEmpty() && !arrayStarted) {
                jg.writeStartArray();
                arrayStarted = true;
            }
            exportNonKeyColumns(row);
        } else {
            exportKeyColumn(row, index);
            visitRow(row, index + 1);
        }
        previousRow = row;
    }

    private void exportKeyColumn(@Nonnull Map<String, Object> row, int index) throws IOException {
        if (previousRowNullOrDifferentKeyValue(row, index)) {
            closePreviousObjects(index);
            if (!processedKeys.contains(keyColumnNames.get(index))) {
                jg.writeStartObject();
                jg.writeFieldName(keyColumnNames.get(index));
                processedKeys.addLast(keyColumnNames.get(index));
                jg.writeStartObject();
            }
            jg.writeFieldName(row.get(keyColumnNames.get(index)).toString());
        }
    }

    private void closePreviousObjects(int index) throws IOException {
        if (previousRow != null) {
            for (int c = index; c < keyColumnNames.size() - 1; c++) {
                jg.writeEndObject();
                jg.writeEndObject();
                if (c == index) {
                    processedKeys.removeLast();
                }
            }
        }
    }

    private void exportNonKeyColumns(@Nonnull Map<String, Object> row) throws IOException {
        jg.writeStartObject();
        for (String key : row.keySet()) {
            if (!keyColumnNames.contains(key)) {
                jg.writeObjectField(key, row.get(key));
            }
        }
        jg.writeEndObject();
    }

    private boolean previousRowNullOrDifferentKeyValue(@Nonnull final Map<String, Object> row, final int index) {
        if (previousRow == null) {
            return true;
        }
        for (int i = 0; i <= index; i++) {
            if (!previousRow.get(keyColumnNames.get(i)).equals(row.get(keyColumnNames.get(i)))) {
                return true;
            }
        }
        return false;
    }

}

/*

key2 | name
===================
1    | name1
2    | name2

ByKey2 = {"key2": {"1", {"name": "name1"},
                   "2", {"name": "name2"}
                  }
         };


key1 | key2 | name
===================
a    | 1    | name1
a    | 2    | name2
b    | 1    | name3
b    | 2    | name4

ByKey1ByKey2 = {"key1": {"a": {"key2": {"1": {"name": "name1"},
                                        "2": {"name": "name2"}
                                       }
                              },
                         "b": {"key2": {"1": {"name": "name3"},
                                        "2": {"name": "name4"}
                                       }
                              }
                        }
               };

visitRow({"key1": "a", "key2": 1, "name": "name1"}, ["key1", "key2"]):
- For all key columns in order, if new value:
  - writeStartObject("key1").writeStartObject("a") // New value for key1.  // 2
  - writeStartObject("key2").writeStartObject("1") // New value for key2.  // 4
- For all remaining columns:
  - write("name": "name1");
visitRow({"key1": "a", "key2": 2, "name": "name2"}, ["key1", "key2"]):
- For all key columns in order, if new value:
  - if (prevKeyChanged): writeStartObject
  - writeStartObject("2")                          // prevKey not changend
- For all remaining columns:
  - write("name": "name1");



write("name", "name1")

ByKey1ByKey2.key1["a"].key2["1"].name = "name1";


 */
