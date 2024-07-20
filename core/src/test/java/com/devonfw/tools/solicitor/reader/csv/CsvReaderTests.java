/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public class CsvReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(CsvReaderTests.class);

  Application application;

  private Logger logger;

  public CsvReaderTests() {

    // configuration settings
    Map<String, String> configuration = new HashMap<String, String>();
    configuration.put("groupId", "0");
    configuration.put("artifactId", "1");
    configuration.put("version", "2");
    configuration.put("license", "3");
    configuration.put("licenseUrl", "4");
    configuration.put("allowDuplicateHeaderNames", "false");
    configuration.put("allowMissingColumnNames", "false");
    configuration.put("autoFlush", "false");
    configuration.put("commentMarker", "#");
    configuration.put("delimiter", ";");
    configuration.put("escape", "!");
    configuration.put("ignoreEmptyLines", "true");
    configuration.put("ignoreHeaderCase", "true");
    configuration.put("ignoreSurroundingSpaces", "true");
    configuration.put("nullString", "newNullString");
    configuration.put("quote", "'");
    configuration.put("recordSeparator", "\n");
    configuration.put("skipHeaderRecord", "true");
    configuration.put("trailingDelimiter", "true");
    configuration.put("trim", "true");

    ModelFactory modelFactory = new ModelFactoryImpl();

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
    CsvReader csvr = new CsvReader();
    csvr.setModelFactory(modelFactory);
    csvr.setInputStreamFactory(new FileInputStreamFactory());
    csvr.readInventory("csv", "src/test/resources/csvlicenses.csv", this.application, UsagePattern.DYNAMIC_LINKING,
        "maven", "maven", configuration);

  }

  @Test
  public void findArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getGroupId().equals("org.antlr.runtime") && //
          ap.getArtifactId().equals("antlr'") && //
          ap.getVersion().equals("4.6.0")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void readFileAndCheckSize() {

    LOG.info(this.application.toString());
    assertEquals(5, this.application.getApplicationComponents().size());
  }

  @Test
  public void testFindLicense() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("xtend") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")
          && ap.getRawLicenses().get(0).getLicenseUrl().equals("https://spdx.org/licenses/MIT#licenseText")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  /**
   * Tests if packageUrl for maven has been created. Reader runs with config.
   */
  @Test
  public void testFindMavenPackageUrl() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("albireo") && ap.getPackageUrl().equals("pkg:maven/org.eclipse/albireo@0.0.3")) {
        found = true;
        break;
      }
    }
    assertTrue(found);

  }

  /**
   * Tests if packageUrl for npm has been created. Reader runs without config.
   */
  @Test
  public void testFindNpmPackageUrl() {

    Application application;
    ModelFactory modelFactory = new ModelFactoryImpl();
    application = modelFactory.newApplication("testAppNpm", "0.0.0.TEST", "1.1.2111", "http://bla.com", "NPM");
    CsvReader csvr = new CsvReader();
    csvr.setModelFactory(modelFactory);
    csvr.setInputStreamFactory(new FileInputStreamFactory());
    csvr.readInventory("csv", "src/test/resources/csvlicenses_npm.csv", application, UsagePattern.DYNAMIC_LINKING,
        "npm", "npm", null);

    List<ApplicationComponent> lapc = application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("@babel/code-frame")
          && ap.getPackageUrl().equals("pkg:npm/%40babel/code-frame@7.8.3")) {
        found = true;
        break;
      }
    }
    assertTrue(found);

  }

  /**
   * Tests if packageUrl for pypi has been created. Reader runs without config.
   */
  @Test
  public void testFindPypiPackageUrl() {

    Application application;
    ModelFactory modelFactory = new ModelFactoryImpl();
    application = modelFactory.newApplication("testAppPypi", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python");
    CsvReader csvr = new CsvReader();
    csvr.setModelFactory(modelFactory);
    csvr.setInputStreamFactory(new FileInputStreamFactory());
    csvr.readInventory("csv", "src/test/resources/csvlicenses_pypi.csv", application, UsagePattern.DYNAMIC_LINKING,
        "pypi", "pypi", null);

    List<ApplicationComponent> lapc = application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("python-dateutil") && ap.getPackageUrl().equals("pkg:pypi/python-dateutil@2.8.1")) {
        found = true;
        break;
      }
    }
    assertTrue(found);

  }

  /**
   * Tests if reader crashes with correct warn message if no packageType is given. Reader runs without config.
   */
  @Test
  public void testNullPackageUrl() {

    String expectedLogMessage = "The packageType is null or empty";

    Application application;
    ModelFactory modelFactory = new ModelFactoryImpl();
    application = modelFactory.newApplication("testAppNull", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Python");
    CsvReader csvr = new CsvReader();
    csvr.setModelFactory(modelFactory);
    csvr.setInputStreamFactory(new FileInputStreamFactory());

    this.logger = mock(Logger.class);
    csvr.setLogger(this.logger); // Set the mock logger

    try {
      csvr.readInventory("csv", "src/test/resources/csvlicenses_pypi.csv", application, UsagePattern.DYNAMIC_LINKING,
          null, null, null);
    } catch (Exception e) {
      // Capture the log messages
      ArgumentCaptor<String> logEntry1 = ArgumentCaptor.forClass(String.class);
      verify(logger).warn(logEntry1.capture());

      // Assert the log message
      assertEquals(expectedLogMessage, logEntry1.getValue());
    }

  }

}
