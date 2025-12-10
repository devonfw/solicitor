/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.ort;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

public class OrtReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(OrtReaderTests.class);

  Application application;

  public OrtReaderTests() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python",
        "#default#");
    OrtReader pr = new OrtReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("ort", "src/test/resources/analyzer-result.json", this.application, UsagePattern.DYNAMIC_LINKING,
        null, null);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("testArtifactId") && //
          ap.getVersion().equals("testVersion")) {
        found = true;
        assertEquals("pkg:maven/testGroupId/testArtifactId@testVersion", ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(1, this.application.getApplicationComponents().size());
  }

  @Test
  public void readFileAndCheckSizeWithFilter() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    // as we have only one package i9n the file check for matching and non matching filter to make sure
    // the logic does not always filter out if a filter is given
    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Python", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:maven/test_misspelled_GroupId/.*");
    OrtReader pr = new OrtReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("ort", "src/test/resources/analyzer-result.json", application, UsagePattern.DYNAMIC_LINKING, null,
        configuration);

    assertEquals(1, application.getApplicationComponents().size());

    application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python",
        "#default#");
    configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:maven/testGroupId/.*");
    pr = new OrtReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("ort", "src/test/resources/analyzer-result.json", application, UsagePattern.DYNAMIC_LINKING, null,
        configuration);

    assertEquals(0, application.getApplicationComponents().size());
  }

  @Test
  public void testFindLicenseIfSingle() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("testArtifactId")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("Apache License, Version 2.0")) {
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
      if (ap.getArtifactId().equals("testArtifactId") && ap.getOssHomepage().equals("https://test.com/test")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

}
