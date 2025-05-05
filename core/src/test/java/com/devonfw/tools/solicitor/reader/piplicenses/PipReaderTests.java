/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.piplicenses;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    pr.readInventory("pip", "src/test/resources/pipReport.json", this.application, UsagePattern.DYNAMIC_LINKING, "pip",
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
    pr.readInventory("pip", "src/test/resources/pipReport.json", application, UsagePattern.DYNAMIC_LINKING, "pip", null,
        configuration);

    assertEquals(1, application.getApplicationComponents().size());
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
    assertNull(pr.determineLicenseInfo(null, null));
  }

}
