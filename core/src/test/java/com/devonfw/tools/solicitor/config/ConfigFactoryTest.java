/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ConfigFactory}.
 */
public class ConfigFactoryTest {

  /**
   * Test method for {@link com.devonfw.tools.solicitor.config.ConfigFactory#getUrlPath(java.lang.String)}.
   */
  @Test
  public void testGetUrlPath() {

    assertEquals(".", ConfigFactory.getUrlPath("file:foo.txt"));
    assertEquals(".", ConfigFactory.getUrlPath("classpath:some/path/foo.txt"));
    assertEquals("some/path", ConfigFactory.getUrlPath("file:some/path/foo.txt"));
    assertEquals(".", ConfigFactory.getUrlPath("some/path/foo.txt"));
  }

}
