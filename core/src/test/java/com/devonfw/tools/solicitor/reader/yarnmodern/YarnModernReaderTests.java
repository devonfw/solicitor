/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.yarnmodern;

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

/**
 * Tests for {@link YarnModernReader}.
 */
public class YarnModernReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(YarnModernReaderTests.class);

  Application application;

  /**
   * The constructor.
   */
  public YarnModernReaderTests() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular");
    YarnModernReader yr = new YarnModernReader();
    yr.setModelFactory(modelFactory);
    yr.setInputStreamFactory(new FileInputStreamFactory());
    yr.readInventory("yarn-modern", "src/test/resources/yarnModernReport.json", this.application,
        UsagePattern.STATIC_LINKING, "npm", "npm", null);

  }

  /**
   * Check the read in data.
   */
  @Test
  public void findArtifact() {

    assertArtifactExists("@babel/runtime", //
        "6.15.7", //
        "pkg:npm/%40babel/runtime@6.15.7", //
        "MIT", "https://babel.dev/docs/en/next/babel-runtime", //
        "https://github.com/babel/babel", //
        "https://raw.githubusercontent.com/babel/babel/master/LICENSE");
    assertArtifactExists("@floating-ui/react-dom", //
        "2.2.0", //
        "pkg:npm/%40floating-ui/react-dom@2.2.0", //
        "MIT", "https://floating-ui.com/docs/react-dom", //
        "https://github.com/floating-ui/floating-ui", //
        "https://raw.githubusercontent.com/floating-ui/floating-ui/master/LICENSE");
    assertArtifactExists("@humanwhocodes/config-array", //
        "0.9.17", //
        "pkg:npm/%40humanwhocodes/config-array@0.9.17", //
        "Apache-2.0", //
        "https://github.com/humanwhocodes/config-array#readme", //
        "https://github.com/humanwhocodes/config-array", //
        "https://raw.githubusercontent.com/humanwhocodes/config-array/master/LICENSE");
    assertArtifactExists("typescript", //
        "4.3.7", //
        "pkg:npm/typescript@4.3.7", //
        "Apache-2.0", //
        "https://www.typescriptlang.org/", //
        "https://github.com/Microsoft/TypeScript", //
        "https://raw.githubusercontent.com/Microsoft/TypeScript/master/LICENSE");
    assertArtifactExists("my-space", //
        "none", //
        "pkg:npm/my-space@none", //
        "UNKNOWN", //
        "", //
        null, //
        null);
  }

  private void assertArtifactExists(String artifactId, String version, String packageUrl, String license,
      String ossHomepage, String sourceRepoUrl, String licenseUrl) {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals(artifactId) && //
          ap.getVersion().equals(version)) {
        found = true;
        assertEquals(packageUrl, ap.getPackageUrl());
        assertEquals(license, ap.getRawLicenses().get(0).getDeclaredLicense());
        assertEquals(ossHomepage, ap.getOssHomepage());
        assertEquals(sourceRepoUrl, ap.getSourceRepoUrl());
        assertEquals(licenseUrl, ap.getRawLicenses().get(0).getLicenseUrl());
        assertEquals("npm", ap.getRepoType());
        assertEquals(UsagePattern.STATIC_LINKING, ap.getUsagePattern());

        break;
      }
    }
    assertTrue(found);
  }

  /**
   * Test if the number of read components is correct.
   */
  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(5, this.application.getApplicationComponents().size());
  }

}
