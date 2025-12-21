/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.yarn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

public class YarnReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(YarnReaderTests.class);

  Application application;

  public YarnReaderTests() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    YarnReader yr = new YarnReader();
    yr.setModelFactory(modelFactory);
    yr.setInputStreamFactory(new FileInputStreamFactory());
    yr.readInventory("yarn", "src/test/resources/yarnReport.json", this.application, UsagePattern.DYNAMIC_LINKING,
        false, null, null);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("test") && //
          ap.getVersion().equals("11.0.0")) {
        found = true;
        assertEquals("pkg:npm/test@11.0.0", ap.getPackageUrl().toString());
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
        "Angular", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:npm/%40test/.*");
    YarnReader yr = new YarnReader();
    yr.setModelFactory(modelFactory);
    yr.setInputStreamFactory(new FileInputStreamFactory());
    yr.readInventory("yarn", "src/test/resources/yarnReport.json", application, UsagePattern.DYNAMIC_LINKING, false,
        null, configuration);

    assertEquals(1, application.getApplicationComponents().size());
  }

  @Test
  public void testUsagePatternAndOssModified() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    YarnReader yr = new YarnReader();
    yr.setModelFactory(modelFactory);
    yr.setInputStreamFactory(new FileInputStreamFactory());
    yr.readInventory("yarn", "src/test/resources/yarnReport.json", this.application, UsagePattern.DYNAMIC_LINKING,
        false, null, null);
    assertEquals(UsagePattern.DYNAMIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertFalse(this.application.getApplicationComponents().get(0).isOssModified());

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    yr = new YarnReader();
    yr.setModelFactory(modelFactory);
    yr.setInputStreamFactory(new FileInputStreamFactory());
    yr.readInventory("yarn", "src/test/resources/yarnReport.json", this.application, UsagePattern.STATIC_LINKING, true,
        null, null);
    assertEquals(UsagePattern.STATIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertTrue(this.application.getApplicationComponents().get(0).isOssModified());
  }

  @Test
  public void testFindLicenseIfSingle() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("@test/testing") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
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
      if (ap.getArtifactId().equals("test") && ap.getOssHomepage().equals("http://test.com")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testSourceRepoUrlWhichIsGiven() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("@test/testing")
          && ap.getSourceRepoUrl().equals("https://yarnpkg.com/package/@test/testing")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testHomepageWhichIsNotGiven() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("@test/testing") && ap.getOssHomepage().equals("")) {
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
      if (ap.getArtifactId().equals("test") && ap.getRawLicenses().get(0).getLicenseUrl()
          .equals("https://raw.githubusercontent.com/mrtest/test/master/LICENSE")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

}
