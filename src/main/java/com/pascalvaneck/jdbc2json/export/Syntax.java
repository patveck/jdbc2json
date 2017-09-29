package com.pascalvaneck.jdbc2json.export;

import com.pascalvaneck.jdbc2json.db.DbVendor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;

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
    COMMONJS("exports.%s = ", ";", ".js"),

    /**
     * @see <a href="https://github.com/amdjs/amdjs-api/wiki/AMD"></a>
     */
    AMD("define({%s: ", ");", ".js"),

    /**
     * ECMAScript 2015 a.k.a. ECMAScript 6th Edition (ES6).
     */
    ES2015("export const %s = ", ";", ".js"),

    /**
     * TypeScript module (actually the same as ES2015).
     */
    TYPESCRIPT("export const %s = ", ";", ".ts");

    private static final Log LOG = LogFactory.getLog(DbVendor.class);

    private final String prefix;

    private final String postfix;

    private final String extension;

    Syntax(final String prefix, final String postfix, final String extension) {
        this.prefix = prefix;
        this.postfix = postfix;
        this.extension = extension;
    }

    @Nonnull
    public String getPrefix(@Nonnull final String tableName) {
        return String.format(prefix, tableName);
    }

    @Nonnull
    public String getPostfix() {
        return postfix;
    }

    public String getExtension() {
        return extension;
    }

}
