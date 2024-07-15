/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.ort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python");
    OrtReader pr = new OrtReader();
    pr.setModelFactory(modelFactory);
    pr.setInputStreamFactory(new FileInputStreamFactory());
    pr.readInventory("ort", "src/test/resources/analyzer-result.json", this.application, UsagePattern.DYNAMIC_LINKING, "ort", "ort",
        null);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("testArtifactId") && //
          ap.getVersion().equals("testVersion")) {
        found = true;
        assertEquals("pkg:maven/testGroupId/testArtifactId@testVersion", ap.getPackageUrl());
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
  public void testFindLicenseIfSingle() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("testArtifactId") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("Apache License, Version 2.0")) {
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
