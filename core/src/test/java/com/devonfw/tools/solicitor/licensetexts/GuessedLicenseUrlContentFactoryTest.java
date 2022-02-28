/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link GuessedLicenseUrlContentFactory}.
 */
public class GuessedLicenseUrlContentFactoryTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContentFactory#fromString(java.lang.String)}.
   */
  @Test
  public void testFromString() {

    GuessedLicenseUrlContentFactory factory = new GuessedLicenseUrlContentFactory();

    GuessedLicenseUrlContent result = factory.fromString("Testtext\n-----------\n1234\n5678");
    assertEquals(result.getGuessedUrl(), "Testtext");
    assertEquals(result.getAuditInfo(), "1234\n5678");
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContentFactory#fromString(java.lang.String)}.
   */
  @Test
  public void testFromString1() {

    GuessedLicenseUrlContentFactory factory = new GuessedLicenseUrlContentFactory();

    GuessedLicenseUrlContent result = factory.fromString("\n-----------\n1234\n5678");
    assertNull(result.getGuessedUrl());
    assertEquals(result.getAuditInfo(), "1234\n5678");
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContentFactory#fromString(java.lang.String)}.
   */
  @Test
  public void testFromString2() {

    GuessedLicenseUrlContentFactory factory = new GuessedLicenseUrlContentFactory();

    GuessedLicenseUrlContent result = factory.fromString("Testtext\n-----------\n");
    assertEquals(result.getGuessedUrl(), "Testtext");
    assertNull(result.getAuditInfo());
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContentFactory#fromString(java.lang.String)}.
   */
  @Test
  public void testFromString3() {

    GuessedLicenseUrlContentFactory factory = new GuessedLicenseUrlContentFactory();

    GuessedLicenseUrlContent result = factory.fromString(null);
    assertNull(result.getGuessedUrl());
    assertNull(result.getAuditInfo());
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContentFactory#fromString(java.lang.String)}.
   */
  @Test
  public void testFromString4() {

    GuessedLicenseUrlContentFactory factory = new GuessedLicenseUrlContentFactory();

    GuessedLicenseUrlContent result = factory.fromString("");
    assertNull(result.getGuessedUrl());
    assertNull(result.getAuditInfo());
  }
}
