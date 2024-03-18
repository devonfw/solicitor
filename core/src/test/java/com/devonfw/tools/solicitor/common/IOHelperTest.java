package com.devonfw.tools.solicitor.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link IOHelper}.
 *
 */
class IOHelperTest {

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.IOHelper#secureFilePath(java.lang.String, java.lang.String[])}.
   */
  @Test
  void testSecureFilePath() {

    assertEquals(fixSep("base"), IOHelper.secureFilePath("base"));
    assertEquals(fixSep("base/r1"), IOHelper.secureFilePath("base", "r1"));
    assertEquals(fixSep("base/r1/r2"), IOHelper.secureFilePath("base", "r1", "r2"));
    assertEquals(fixSep("base/r1/r2"), IOHelper.secureFilePath("base", "r1///", "r2/././"));
    assertEquals(fixSep("/base/r1/r2"), IOHelper.secureFilePath("/base", "r1///", "r2/././"));

    assertThrows(IllegalArgumentException.class, () -> {
      IOHelper.secureFilePath("base", "/r1", "r2");
    });

    assertThrows(IllegalArgumentException.class, () -> {
      IOHelper.secureFilePath("base", "../r1", "r2");
    });

    assertThrows(IllegalArgumentException.class, () -> {
      IOHelper.secureFilePath("base", "r1", "../r2");
    });

    assertEquals(fixSep("base/r1/r2"), IOHelper.secureFilePath("base", "a/../r1", "r2"));

    assertThrows(IllegalArgumentException.class, () -> {
      IOHelper.secureFilePath("base", "a/../../r1", "r2");
    });
  }

  /**
   * Returns the given strings with all occurrences of <code>/</code> or <code>\\</code> to be replaced by the system
   * dependent file separator character. This is required to handle differences between Windows and Unix.
   *
   * @param input the origin string
   * @return the fixed string
   */
  private static String fixSep(String input) {

    return input.replace('/', File.separatorChar).replace('\\', File.separatorChar);
  }

}
