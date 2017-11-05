package com.pascalvaneck.jdbc2json;

import com.pascalvaneck.jdbc2json.db.DbVendor;
import com.pascalvaneck.jdbc2json.export.Syntax;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Jdbc2JsonTest {

    private Jdbc2Json jdbc2Json;

    @Before
    public void setUp() {
        jdbc2Json = Jdbc2Json.getInstance();
    }

    @Test
    public void evaluatesExpression() {
        assertNotNull("Jdbc2Json should be instantiated", jdbc2Json);
    }

    @Test
    public void testEnumArguments() throws CmdLineException {
        final String[] args = {"-e", "MySQL", "-f", "ES2015", "-h", "localhost", "-u", "user", "-p", "pw", "mydb"};
        jdbc2Json.parseArguments(args);
        assertEquals( "Hostname must be 'localhost'","localhost", jdbc2Json.getDbHostname());
        assertEquals("DB engine must be set", DbVendor.MYSQL, jdbc2Json.getDbVendor());
        assertEquals("Syntax is ES2015.", Syntax.ES2015, jdbc2Json.getSyntax());
        assertEquals( "Database name is \"mydb\".", "mydb", jdbc2Json.getDbName());
        assertEquals("Output dir is current directory.", (new File(System.getProperty("user.dir")).getAbsolutePath()),
                     jdbc2Json.getOutputDir().getAbsolutePath());
    }

    @Test
    public void testIncludesAndExcludes() throws CmdLineException {
        final String[] args = {"-e", "MySQL", "-I", "table1,table2", "--exclude=table3", "-f", "ES2015", "-h", "localhost", "-u", "user", "-p", "pw", "mydb"};
        jdbc2Json.parseArguments(args);
        assertEquals( "There are two includes.",2, jdbc2Json.getIncludes().size());
        assertEquals("First include is 'table1'.", "table1", jdbc2Json.getIncludes().get(0));
        assertEquals("Second include is 'table2'.", "table2", jdbc2Json.getIncludes().get(1));
        assertEquals( "There is one excludes.",1, jdbc2Json.getExcludes().size());
        assertEquals("First include is 'table3'.", "table3", jdbc2Json.getExcludes().get(0));
    }

}
