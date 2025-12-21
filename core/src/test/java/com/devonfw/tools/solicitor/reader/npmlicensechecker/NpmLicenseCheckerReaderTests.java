/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.npmlicensechecker;

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

public class NpmLicenseCheckerReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(NpmLicenseCheckerReaderTests.class);

  Application application;

  public NpmLicenseCheckerReaderTests() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    NpmLicenseCheckerReader gr = new NpmLicenseCheckerReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("npm-license-checker", "src/test/resources/npmLicenseCheckerReport.json", this.application,
        UsagePattern.DYNAMIC_LINKING, false, null, null);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo") && //
          ap.getVersion().equals("0.0.1")) {
        found = true;
        assertEquals("pkg:npm/foo@0.0.1", ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(3, this.application.getApplicationComponents().size());

  }

  @Test
  public void readFileAndCheckSizeWithFilter() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Angular", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:npm/foo\\-bar@.*");
    NpmLicenseCheckerReader gr = new NpmLicenseCheckerReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("npm-license-checker", "src/test/resources/npmLicenseCheckerReport.json", application,
        UsagePattern.DYNAMIC_LINKING, false, null, configuration);

    assertEquals(2, application.getApplicationComponents().size());

  }

  @Test
  public void testUsagePatternAndOssModified() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    NpmLicenseCheckerReader gr = new NpmLicenseCheckerReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("npm-license-checker", "src/test/resources/npmLicenseCheckerReport.json", this.application,
        UsagePattern.DYNAMIC_LINKING, false, null, null);
    assertEquals(UsagePattern.DYNAMIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertFalse(this.application.getApplicationComponents().get(0).isOssModified());

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    gr = new NpmLicenseCheckerReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("npm-license-checker", "src/test/resources/npmLicenseCheckerReport.json", this.application,
        UsagePattern.STATIC_LINKING, true, null, null);
    assertEquals(UsagePattern.STATIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertTrue(this.application.getApplicationComponents().get(0).isOssModified());
  }

  @Test
  public void testFindLicenseIfSingle() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindLicensesIfMultiple() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo-bar") && ap.getRawLicenses().get(0).getDeclaredLicense().matches("AFLv2.1|BSD")
          && ap.getRawLicenses().get(1).getDeclaredLicense().matches("AFLv2.1|BSD")) {
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
      if (ap.getArtifactId().equals("foo") && ap.getOssHomepage().equals("http://www.somebody.com/")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testHomepageNotGiven() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo-bar") && ap.getOssHomepage() == null) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testSourceRepoFromRepo() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo") && ap.getSourceRepoUrl().equals("https://github.com/somebody/foo")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testLicenseUrlIfLicenseFileFoundAndGithub() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo") && ap.getRawLicenses().get(0).getLicenseUrl()
          .equals("https://raw.githubusercontent.com/somebody/foo/master/LICENSE")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testLicenseUrlIfNoLicenseFile() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("foo-bar")
          && ap.getRawLicenses().get(0).getLicenseUrl().equals("https://github.com/nobody/foo-bar")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

}
