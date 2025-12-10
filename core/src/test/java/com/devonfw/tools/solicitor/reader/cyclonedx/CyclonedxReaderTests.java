
package com.devonfw.tools.solicitor.reader.cyclonedx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

/**
 * This class contains JUnit test methods for the {@link CyclonedxReader} class.
 */
public class CyclonedxReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(CyclonedxReaderTests.class);

  CyclonedxReader cdxr = new CyclonedxReader();

  ModelFactory modelFactory = new ModelFactoryImpl();

  /**
   * Test the {@link CyclonedxReader#readInventory()} method. Input file is an SBOM containing maven components. Mock
   * the case, that a maven PurlHandler exists.
   */
  @Test
  public void readMavenFileAndCheckSize() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("maven", "src/test/resources/mavensbom.json", application, UsagePattern.DYNAMIC_LINKING,
        null, null);
    LOG.info(application.toString());

    assertEquals(32, application.getApplicationComponents().size());

    boolean found = false;
    for (ApplicationComponent ap : application.getApplicationComponents()) {
      if (ap.getGroupId().equals("org.springframework.boot") && //
          ap.getArtifactId().equals("spring-boot-starter-logging") && //
          ap.getVersion().equals("2.3.3.RELEASE")) {
        found = true;
        assertEquals("pkg:maven/org.springframework.boot/spring-boot-starter-logging@2.3.3.RELEASE?type=jar",
            ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);
  }

  /**
   * Test the {@link CyclonedxReader#readInventory()} method. Input file is an SBOM containing maven components. Mock
   * the case, that a maven PurlHandler exists. Test the case that a reader filter exists.
   */
  @Test
  public void readMavenFileAndCheckSizeWithFilter() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:maven/.*/spring-boot-starter-web@.*");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("maven", "src/test/resources/mavensbom.json", application, UsagePattern.DYNAMIC_LINKING,
        null, configuration);
    LOG.info(application.toString());

    assertEquals(31, application.getApplicationComponents().size());

  }

  /**
   * Test the {@link CyclonedxReader#readInventory()} method. Input file is an SBOM containing maven components. Mock
   * the case, that a maven PurlHandler does not exist.
   */
  @Test
  public void readMavenFileAndCheckSizeNegative() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("maven", "src/test/resources/mavensbom.json", application, UsagePattern.DYNAMIC_LINKING,
        null, null);
    LOG.info(application.toString());

    assertEquals(32, application.getApplicationComponents().size());

  }

  /**
   * Test the {@link CyclonedxReader#readInventory()} method. Input file is an SBOM containing maven components. Check
   * one entry for correct content.
   */
  @Test
  public void readMavenFileAndCheckSingleContentSize() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("maven", "src/test/resources/mavensbom.json", application, UsagePattern.DYNAMIC_LINKING,
        null, null);
    LOG.info(application.toString());

    assertEquals(32, application.getApplicationComponents().size());

    boolean found = false;
    for (ApplicationComponent ap : application.getApplicationComponents()) {
      if (ap.getGroupId().equals("ch.qos.logback") && //
          ap.getArtifactId().equals("logback-classic") && //
          ap.getVersion().equals("1.2.3")) {
        found = true;
        assertEquals(UsagePattern.DYNAMIC_LINKING, ap.getUsagePattern());
        assertEquals("pkg:maven/ch.qos.logback/logback-classic@1.2.3?type=jar", ap.getPackageUrl().toString());
        assertEquals(4, ap.getRawLicenses().size());
        boolean l1found = false;
        boolean l2found = false;
        boolean l3found = false;
        boolean l4found = false;

        for (RawLicense rl : ap.getRawLicenses()) {
          if (rl.getDeclaredLicense().equals("EPL-1.0")) {
            l1found = true;
            assertNull(rl.getLicenseUrl());
          }
          if (rl.getDeclaredLicense().equals("EPL-2.0")) {
            l2found = true;
            assertEquals("some url", rl.getLicenseUrl());
          }
          if (rl.getDeclaredLicense().equals("someOtherLicense")) {
            l3found = true;
            assertNull(rl.getLicenseUrl());
          }
          if (rl.getDeclaredLicense().equals("GNU Lesser General Public License")) {
            l4found = true;
            assertEquals("http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html", rl.getLicenseUrl());

          }
        }
        assertTrue(l1found && l2found && l3found && l4found);
        break;
      }
    }
    assertTrue(found);
  }

  /**
   * Test the {@link CyclonedxReader#readInventory()} method. Input file is an SBOM containing npm components. Mock the
   * case, that a npm PurlHandler exists.
   */
  @Test
  public void readNpmFileAndCheckSize() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Angular", "#default#");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("npm", "src/test/resources/npmsbom.json", application, UsagePattern.DYNAMIC_LINKING, null,
        null);
    LOG.info(application.toString());

    assertEquals(74, application.getApplicationComponents().size());

    boolean found = false;
    for (ApplicationComponent ap : application.getApplicationComponents()) {
      if (ap.getArtifactId().equals("async") && ap.getVersion().equals("3.2.4")) {
        found = true;
        assertEquals("pkg:npm/async@3.2.4", ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);
  }

  /**
   * Test the {@link CyclonedxReader#readInventory()} method. Input file is an SBOM containing expressions.
   */
  @Test
  public void readExpression() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Angular", "#default#");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("npm", "src/test/resources/expressionsbom.json", application, UsagePattern.DYNAMIC_LINKING,
        null, null);
    LOG.info(application.toString());

    boolean found = false;

    for (ApplicationComponent ap : application.getApplicationComponents()) {
      if (ap.getArtifactId().equals("hk2-locator") && ap.getVersion().equals("2.5.0-b42")) {
        found = true;
        assertEquals("(CDDL-1.0 OR GPL-2.0-with-classpath-exception)", ap.getRawLicenses().get(0).getDeclaredLicense());

      }
    }
    assertTrue(found);
  }

  /**
   * Test the
   * {@link CyclonedxReader#readInventory(String, String, Application, UsagePattern, String, String, java.util.Map)}
   * method. Input file is a minimal SBOM containing only two components with minimal information.
   */
  @Test
  public void readMinimalFile() {

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.readInventory("maven", "src/test/resources/cyclonedx_minimal.json", application,
        UsagePattern.DYNAMIC_LINKING, null, null);

    assertEquals(2, application.getApplicationComponents().size());
    assertEquals("foo", application.getApplicationComponents().get(0).getArtifactId());
    assertEquals("bar", application.getApplicationComponents().get(1).getArtifactId());
    assertNull(application.getApplicationComponents().get(1).getPackageUrl());
  }

}
