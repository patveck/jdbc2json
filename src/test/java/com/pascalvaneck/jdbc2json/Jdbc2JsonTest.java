package com.pascalvaneck.jdbc2json;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class Jdbc2JsonTest {

  @Test
  public void evaluatesExpression() {
    Jdbc2Json jdbc2Json = new Jdbc2Json();
    assertNotNull("Jdbc2Json should be instantiated", jdbc2Json);
  }
}
