package com.pascalvaneck.mysql2json;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class AppTest {

  @Test
  public void evaluatesExpression() {
    App app = new App();
    assertNotNull("App should be instantiated", app);
  }
}
