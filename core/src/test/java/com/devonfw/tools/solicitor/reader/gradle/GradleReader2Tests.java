/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle;


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

public class GradleReader2Tests {
  private static final Logger LOG = LoggerFactory.getLogger(GradleReader2Tests.class);

  Application application;

  public GradleReader2Tests() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
    GradleReader2 gr = new GradleReader2();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("gradle2", "src/test/resources/licenseReport.json", this.application, UsagePattern.DYNAMIC_LINKING,
        "maven", null);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getGroupId().equals("org.apache.xmlbeans") && //
          ap.getArtifactId().equals("xmlbeans") && //
          ap.getVersion().equals("3.0.2")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(15, this.application.getApplicationComponents().size());
  }

  @Test
  public void testFindLicense() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("vavr-jackson")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("The Apache Software License, Version 2.0")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindLicense2() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("curvesapi")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("BSD License")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindLicense3() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("jackson-core") && ap.getVersion().equals("2.9.4")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("The Apache Software License, Version 2.0")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }
}
