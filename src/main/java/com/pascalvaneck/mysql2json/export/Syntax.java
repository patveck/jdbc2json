package com.pascalvaneck.mysql2json.export;

import com.pascalvaneck.mysql2json.db.DbVendor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  Colors that can be used.
 *  <li>{@link #COMMONJS}</li>
 *  <li>{@link #AMD}</li>
 *  <li>{@link #ES2015}</li>
 *  <li>{@link #TYPESCRIPT}</li>
 */
public enum Syntax {

    /**
     * @see <a href="http://wiki.commonjs.org/wiki/Modules/1.1"></a>
     */
    COMMONJS("exports.${tableName} = ${jsonObject};", ".js"),

    /**
     * @see <a href="https://github.com/amdjs/amdjs-api/wiki/AMD"></a>
     */
    AMD("define({${tableName}: ${jsonObject}});", ".js"),

    /**
     * ECMAScript 2015 a.k.a. ECMAScript 6th Edition (ES6).
     */
    ES2015("export const ${tableName} = ${jsonObject};", ".js"),

    /**
     * TypeScript module (actually the same as ES2015).
     */
    TYPESCRIPT("export const ${tableName} = ${jsonObject};", ".ts");

    private static final Log LOG = LogFactory.getLog(DbVendor.class);

    private final String template;

    private final String extension;

    Syntax(final String template, final String extension) {
        this.template = template;
        this.extension = extension;
    }

    public String getTemplate() {
        return template;
    }

    public String getExtension() {
        return extension;
    }
}
