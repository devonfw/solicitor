package com.devonfw.tools.solicitor.componentinfo.curation.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;

/**
 * Tests for {@link CopyrightCuration}. Note that not all possible data constellations are tested but just the main
 * behavior.
 *
 */
class CopyrightCurationTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration#matches(java.lang.String, java.lang.String)}.
   */
  @Test
  void testMatchesStringString() {

    CopyrightCuration cc = new CopyrightCuration();
    cc.setOperation(CurationOperation.ADD);

    assertFalse(cc.matches(null, null));

    cc.setOperation(CurationOperation.REMOVE);
    assertTrue(cc.matches(null, null));

    cc.setPath(".*abc.*");
    cc.setOldCopyright(".*456.*");
    assertTrue(cc.matches("abcd", "4567"));
    assertFalse(cc.matches("bcd", "4567"));
    assertFalse(cc.matches("abcd", "567"));
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration#matches(java.lang.String)}.
   */
  @Test
  void testMatchesString() {

    CopyrightCuration cc = new CopyrightCuration();
    cc.setOperation(CurationOperation.REMOVE);

    assertFalse(cc.matches(null));

    cc.setOperation(CurationOperation.ADD);
    assertTrue(cc.matches(null));
    assertFalse(cc.matches("foo"));

    cc.setPath(".*abc.*");
    assertTrue(cc.matches("abcd"));
    assertFalse(cc.matches("bcd"));
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateNoOp() throws CurationInvalidException {

    // no operation defined
    CopyrightCuration cc = new CopyrightCuration();

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "Operation must not be null for copyright curation");

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateAdd() throws CurationInvalidException {

    CopyrightCuration cc = new CopyrightCuration();

    cc.setOperation(CurationOperation.ADD);

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "For ADD copyright curation newCopyright must be set");

    cc.setNewCopyright("foo");

    cc.validate(); // should be ok

    cc.setOldCopyright("bar");

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "For ADD copyright curation oldCopyright must not be defined");

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateRemove() throws CurationInvalidException {

    CopyrightCuration cc = new CopyrightCuration();

    cc.setOperation(CurationOperation.REMOVE);

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "For REMOVE/REPLACE copyright curation at least one condition must be defined");

    cc.setPath("foo");

    cc.validate(); // should be ok

    cc.setNewCopyright("bar");

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "For REMOVE copyright curation the newCopyright must not be set");

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateReplace() throws CurationInvalidException {

    CopyrightCuration cc = new CopyrightCuration();

    cc.setOperation(CurationOperation.REPLACE);

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "For REMOVE/REPLACE copyright curation at least one condition must be defined");

    cc.setPath("foo");

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      cc.validate();
    }, "For REPLACE copyright curation newCopyright must be set");

    cc.setNewCopyright("bar");

    cc.validate(); // should be ok

  }

}
