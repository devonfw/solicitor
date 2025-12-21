/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.piplicenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public class PipReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(PipReaderTests.class);

  Application application;

  public PipReaderTests() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python",
        "#default#");
    PipLicensesReader pr = new PipLicensesReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("pip", "src/test/resources/pipReport.json", this.application, UsagePattern.DYNAMIC_LINKING, false,
        null, null);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("test") && //
          ap.getVersion().equals("2021.1")) {
        found = true;
        assertEquals("pkg:pypi/test@2021.1", ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(2, this.application.getApplicationComponents().size());
  }

  @Test
  public void readFileAndCheckSizeWithFilter() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Python", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:pypi/test2@.*");
    PipLicensesReader pr = new PipLicensesReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("pip", "src/test/resources/pipReport.json", application, UsagePattern.DYNAMIC_LINKING, false, null,
        configuration);

    assertEquals(1, application.getApplicationComponents().size());
  }

  @Test
  public void testUsagePatternAndOssModified() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python",
        "#default#");
    PipLicensesReader pr = new PipLicensesReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("pip", "src/test/resources/pipReport.json", this.application, UsagePattern.DYNAMIC_LINKING, false,
        null, null);
    assertEquals(UsagePattern.DYNAMIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertFalse(this.application.getApplicationComponents().get(0).isOssModified());

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python",
        "#default#");
    pr = new PipLicensesReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("pip", "src/test/resources/pipReport.json", this.application, UsagePattern.STATIC_LINKING, true,
        null, null);
    assertEquals(UsagePattern.STATIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertTrue(this.application.getApplicationComponents().get(0).isOssModified());
  }

  @Test
  public void testFindLicenseIfSingle() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("test") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testHomepageWhichIsGiven() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("test2") && ap.getOssHomepage().equals("https://github.com/test/test2")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testLicenseUrlWhichIsGiven() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("test2") && ap.getRawLicenses().get(0).getLicenseUrl()
          .equals("https://raw.githubusercontent.com/test/test2/master/LICENSE")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testDetermineLicenseInfo() {

    PipLicensesReader pr = new PipLicensesReader();
    // check behavior if at least one argument is UNKNOWN or null
    assertNull(pr.determineLicenseInfo(null, null));
    assertNull(pr.determineLicenseInfo("UNKNOWN", null));
    assertNull(pr.determineLicenseInfo(null, "UNKNOWN"));
    assertNull(pr.determineLicenseInfo("UNKNOWN", "UNKNOWN"));
    assertEquals("foo", pr.determineLicenseInfo(null, "foo"));
    assertEquals("foo", pr.determineLicenseInfo("foo", null));
    assertEquals("foo", pr.determineLicenseInfo("UNKNOWN", "foo"));
    assertEquals("foo", pr.determineLicenseInfo("foo", "UNKNOWN"));

    // check behavior if at least one argument is an SPDX-ID
    assertEquals("MIT", pr.determineLicenseInfo("MIT", "bar1"));
    assertEquals("MIT", pr.determineLicenseInfo("bar1", "MIT"));
    assertEquals("Apache-2.0", pr.determineLicenseInfo("Apache-2.0", "MIT"));
    assertEquals("MIT", pr.determineLicenseInfo("MIT", "Apache-2.0"));

    // check behavior if both arguments are plain straings
    assertEquals("bar1", pr.determineLicenseInfo("foo", "bar1")); // take the longer argument
    assertEquals("bar1", pr.determineLicenseInfo("bar1", "foo")); // take the longer argument
    assertEquals("bar", pr.determineLicenseInfo("bar", "foo")); // take metadata if length is the same
  }

}
