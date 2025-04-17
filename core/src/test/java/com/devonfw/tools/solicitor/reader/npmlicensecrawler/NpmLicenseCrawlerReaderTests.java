/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.npmlicensecrawler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.DeprecationChecker;
import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public class NpmLicenseCrawlerReaderTests {

  private static final Logger LOG = LoggerFactory.getLogger(NpmLicenseCrawlerReaderTests.class);

  private Application application;

  public NpmLicenseCrawlerReaderTests() {

    ModelFactory modelFactory = new ModelFactoryImpl();
    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular",
        "#default#");
    NpmLicenseCrawlerReader nr = new NpmLicenseCrawlerReader();
    nr.setDeprecationChecker(new DeprecationChecker() {

      @Override
      public void check(boolean warnOnly, String detailsString) {

        // do nothing...
      }
    });
    nr.setModelFactory(modelFactory);
    nr.setInputStreamFactory(new FileInputStreamFactory());
    nr.readInventory("npm", "src/test/resources/npmlicenses.csv", this.application, UsagePattern.DYNAMIC_LINKING, "npm",
        null, null);
  }

  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(68, this.application.getApplicationComponents().size());
  }

  @Test
  public void readFileAndCheckSizeWithFilter() {

    ModelFactory modelFactory = new ModelFactoryImpl();
    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Angular", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:npm/tmp@.*");
    NpmLicenseCrawlerReader nr = new NpmLicenseCrawlerReader();
    nr.setDeprecationChecker(new DeprecationChecker() {

      @Override
      public void check(boolean warnOnly, String detailsString) {

        // do nothing...
      }
    });
    nr.setModelFactory(modelFactory);
    nr.setInputStreamFactory(new FileInputStreamFactory());
    nr.readInventory("npm", "src/test/resources/npmlicenses.csv", application, UsagePattern.DYNAMIC_LINKING, "npm",
        null, configuration);

    assertEquals(65, application.getApplicationComponents().size());
  }

  @Test
  public void testFindArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("to-fast-properties")) {
        found = true;
        assertEquals("pkg:npm/to-fast-properties@1.0.3", ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindDiffrentLicense() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("aws-sign2")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("Apache-2.0")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindDiffrentLicense2() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("autoprefixer") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindDiffrentLicense3() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("yargs") && ap.getVersion().equals("9.0.1")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testFindLicense() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("tough-cookie")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("BSD-3-Clause")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }
}
