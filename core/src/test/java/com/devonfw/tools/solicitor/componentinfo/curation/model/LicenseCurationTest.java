package com.devonfw.tools.solicitor.componentinfo.curation.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration.NewLicenseData;

/**
 * Tests for {@link LicenseCuration}. Note that not all possible data constellations are tested but just the main
 * behavior.
 *
 */
class LicenseCurationTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#matches(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
   */
  @Test
  void testMatchesStringStringStringString() {

    LicenseCuration lc = new LicenseCuration();
    lc.setOperation(CurationOperation.ADD);

    assertFalse(lc.matches(null, null, null, null));

    lc.setOperation(CurationOperation.REMOVE);
    assertTrue(lc.matches(null, null, null, null));

    lc.setPath(".*abc.*");
    lc.setRuleIdentifier(".*def.*");
    lc.setMatchedText(".*123.*");
    lc.setOldLicense(".*456.*");
    assertTrue(lc.matches("abcd", "cdef", "z123", "4567"));
    assertFalse(lc.matches("bcd", "cdef", "z123", "4567"));
    assertFalse(lc.matches("abcd", "cde", "z123", "4567"));
    assertFalse(lc.matches("abcd", "cdef", "z12", "4567"));
    assertFalse(lc.matches("abcd", "cdef", "z123", "567"));

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#matches(java.lang.String)}.
   */
  @Test
  void testMatchesString() {

    LicenseCuration lc = new LicenseCuration();
    lc.setOperation(CurationOperation.REMOVE);

    assertFalse(lc.matches(null));

    lc.setOperation(CurationOperation.ADD);
    assertTrue(lc.matches(null));
    assertFalse(lc.matches("foo"));

    lc.setPath(".*abc.*");
    assertTrue(lc.matches("abcd"));
    assertFalse(lc.matches("bcd"));
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#newLicenseData()}.
   */
  @Test
  void testNewLicenseData() {

    LicenseCuration lc = new LicenseCuration();
    lc.setOperation(CurationOperation.REMOVE);

    assertNull(lc.newLicenseData());

    lc.setOperation(CurationOperation.ADD);
    NewLicenseData nld = lc.newLicenseData();

    assertNull(nld.license);
    assertNull(nld.url);

    lc.setNewLicense("foo");
    lc.setUrl("bar");

    nld = lc.newLicenseData();

    assertEquals("foo", nld.license);
    assertEquals("bar", nld.url);
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateNoOp() throws CurationInvalidException {

    // no operation defined
    LicenseCuration lc = new LicenseCuration();

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "Operation must not be null for license curation");

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateAdd() throws CurationInvalidException {

    LicenseCuration lc = new LicenseCuration();

    lc.setOperation(CurationOperation.ADD);

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For ADD license curation license and url must be set");

    lc.setNewLicense("foo");
    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For ADD license curation license and url must be set");

    lc.setUrl("bar");
    lc.validate(); // should be ok

    lc.setMatchedText("some text");
    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For ADD license curation at neither ruleIdentifier nor matchedText nor oldLicense must be defined");

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateRemove() throws CurationInvalidException {

    LicenseCuration lc = new LicenseCuration();

    lc.setOperation(CurationOperation.REMOVE);

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For REMOVE/REPLACE license curation at least one condition must be defined");

    lc.setMatchedText("some text");

    lc.validate(); // should be ok

    lc.setNewLicense("foo");

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For REMOVE license curation neither newLicense nor url must be set");

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration#validate()}.
   *
   * @throws CurationInvalidException if the curation is not valid
   */
  @Test
  void testValidateReplace() throws CurationInvalidException {

    LicenseCuration lc = new LicenseCuration();

    lc.setOperation(CurationOperation.REPLACE);

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For REMOVE/REPLACE license curation at least one condition must be defined");

    lc.setMatchedText("some text");

    Assertions.assertThrows(CurationInvalidException.class, () -> {
      lc.validate();
    }, "For REPLACE license curation at least license or url must be set");

    lc.setNewLicense("foo");

    lc.validate(); // should be ok

  }
}
