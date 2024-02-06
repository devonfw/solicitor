package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.NormalizedLicenseImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;

/**
 * Test for class {@link ScancodeLicenseMappingInventoryProcessor}.
 */
class ScancodeLicenseMappingInventoryProcessorTest {

  private ModelFactoryImpl mf;

  private NormalizedLicenseImpl nl;

  private ApplicationComponent ac;

  private ScancodeLicenseMappingInventoryProcessor objectUnderTest;

  private Statistics stat;

  void initObjects(String origin, String dataStatus, String license) {

    // provide required ModelFactory
    this.mf = Mockito.mock(ModelFactoryImpl.class);
    this.nl = new NormalizedLicenseImpl();
    Mockito.when(this.mf.newNormalizedLicense(Mockito.any())).thenReturn(this.nl);
    this.objectUnderTest = new ScancodeLicenseMappingInventoryProcessor();
    this.objectUnderTest.setModelFactory(this.mf);
    this.objectUnderTest.setLicenseIdMappingBlacklistRegexes(new String[] { ".*blacklisted.*" });

    this.stat = new Statistics();

    // List<RawLicense> rll = new ArrayList<>();

    // a SPDX raw license
    RawLicense rl = Mockito.mock(RawLicense.class);
    Mockito.when(rl.getOrigin()).thenReturn(origin);
    Mockito.when(rl.getDeclaredLicense()).thenReturn(license);

    // the containing ApplicationComponent
    this.ac = Mockito.mock(ApplicationComponent.class);
    Mockito.when(this.ac.getDataStatus()).thenReturn(dataStatus);
    Mockito.when(this.ac.getRawLicenses()).thenReturn(Collections.singletonList(rl));

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentOriginNotScancode() {

    initObjects("someother", "DA:FOOBAR", "Apache-2.0");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(0)).newNormalizedLicense(Mockito.any());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentDataStatusNull() {

    initObjects("scancode", null, "Apache-2.0");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(0)).newNormalizedLicense(Mockito.any());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentDataStatusNotDA() {

    initObjects("scancode", "NL:FOOBAR", "Apache-2.0");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(0)).newNormalizedLicense(Mockito.any());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentBlacklisted() {

    initObjects("scancode", "DA:FOOBAR", "LicenseRef-scancode-blacklisted-license");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(0)).newNormalizedLicense(Mockito.any());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentLicenseRef() {

    initObjects("scancode", "DA:FOOBAR", "LicenseRef-scancode-something");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(1)).newNormalizedLicense(Mockito.any());
    assertEquals("LicenseRef-scancode-something", this.nl.getNormalizedLicense());
    assertEquals("SCANCODE", this.nl.getNormalizedLicenseType());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentSPDX() {

    initObjects("scancode", "DA:FOOBAR", "Apache-2.0");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(1)).newNormalizedLicense(Mockito.any());
    assertEquals("Apache-2.0", this.nl.getNormalizedLicense());
    assertEquals("OSS-SPDX", this.nl.getNormalizedLicenseType());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#processApplicationComponent(com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor.Statistics)}.
   */
  @Test
  void testProcessApplicationComponentUnknown() {

    initObjects("scancode", "DA:FOOBAR", "Apache-0.9");

    this.objectUnderTest.processApplicationComponent(this.ac, this.stat);

    Mockito.verify(this.mf, Mockito.times(0)).newNormalizedLicense(Mockito.any());

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#isSpdxLicensePossiblyWithException(java.lang.String)}.
   */
  @Test
  void testIsSpdxLicensePossiblyWithException() {

    this.objectUnderTest = new ScancodeLicenseMappingInventoryProcessor();
    assertTrue(this.objectUnderTest.isSpdxLicensePossiblyWithException("Apache-2.0"));
    assertFalse(this.objectUnderTest.isSpdxLicensePossiblyWithException("Apache-1.2"));
    assertFalse(this.objectUnderTest.isSpdxLicensePossiblyWithException("Classpath-exception-2.0"));
    assertFalse(this.objectUnderTest.isSpdxLicensePossiblyWithException("WITH Classpath-exception-2.0"));
    assertTrue(this.objectUnderTest.isSpdxLicensePossiblyWithException("GPL-2.0-only WITH Classpath-exception-2.0"));
    assertFalse(this.objectUnderTest.isSpdxLicensePossiblyWithException("GPL-0.9-only WITH Classpath-exception-2.0"));
    assertFalse(this.objectUnderTest.isSpdxLicensePossiblyWithException("GPL-2.0-only WITH Classpath-exception-1.0"));

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeLicenseMappingInventoryProcessor#isBlacklisted(java.lang.String)}.
   */
  @Test
  void testIsBlacklisted() {

    this.objectUnderTest = new ScancodeLicenseMappingInventoryProcessor();
    this.objectUnderTest.setLicenseIdMappingBlacklistRegexes(new String[] { ".*aa.*", "b.*" });

    assertFalse(this.objectUnderTest.isBlacklisted(""));
    assertFalse(this.objectUnderTest.isBlacklisted("somelicense"));
    assertFalse(this.objectUnderTest.isBlacklisted("abkkkkk"));
    assertTrue(this.objectUnderTest.isBlacklisted("bkkkkk"));
    assertTrue(this.objectUnderTest.isBlacklisted("aa"));
  }

}
