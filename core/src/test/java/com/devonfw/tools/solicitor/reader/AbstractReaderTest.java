package com.devonfw.tools.solicitor.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader.ReaderStatistics;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;

/**
 * Tests for the AbstractReader.
 *
 */
public class AbstractReaderTest {

  private AbstractReader reader;

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {

    this.reader = new AbstractReader() {

      @Override
      public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
          boolean modified, String packageType, Map<String, String> configuration) {

        // dummy, do nothing
      }

      @Override
      public Set<String> getSupportedTypes() {

        // dummy, do nothing
        return null;
      }
    };

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.reader.AbstractReader#isPackageFiltered(com.github.packageurl.PackageURL, java.util.Map)}.
   *
   * @throws MalformedPackageURLException
   */
  @Test
  void testIsPackageFiltered() throws MalformedPackageURLException {

    Map<String, String> configuration;

    PackageURL purl1 = new PackageURL("pkg:maven/mygroup/myartifact@1.2.3");
    PackageURL purl2 = new PackageURL("pkg:maven/mygroup1/myartifact@1.2.3");

    // null configuration must not filter out any packages
    assertFalse(this.reader.isPackageFiltered(purl1, null));
    assertFalse(this.reader.isPackageFiltered(null, null));

    configuration = new HashMap<>();
    // empty configuration must not filter out any packages
    assertFalse(this.reader.isPackageFiltered(purl1, configuration));
    assertFalse(this.reader.isPackageFiltered(null, configuration));

    configuration.put("someParameter", "someValue");
    // non related configuration must not filter out any packages
    assertFalse(this.reader.isPackageFiltered(purl1, configuration));
    assertFalse(this.reader.isPackageFiltered(null, configuration));

    configuration.put("includeFilter", "pkg:maven/mygroup/.*");
    // includeFilter (and non related parameter)
    assertFalse(this.reader.isPackageFiltered(purl1, configuration));
    assertTrue(this.reader.isPackageFiltered(purl2, configuration));
    assertTrue(this.reader.isPackageFiltered(null, configuration));

    configuration.put("excludeFilter", "pkg:maven/mygroup/.*");
    // includeFilter and excludeFilter (and non related parameter)
    assertTrue(this.reader.isPackageFiltered(purl1, configuration));
    assertTrue(this.reader.isPackageFiltered(purl2, configuration));
    assertTrue(this.reader.isPackageFiltered(null, configuration));

    configuration.remove("includeFilter");
    // only excludeFilter (and non related parameter)
    assertTrue(this.reader.isPackageFiltered(purl1, configuration));
    assertFalse(this.reader.isPackageFiltered(purl2, configuration));
    assertFalse(this.reader.isPackageFiltered(null, configuration));

    configuration.remove("excludeFilter");
    configuration.put("includeFilter", "");
    // only includeFilter which matches to null/empty packageURL (and non related parameter)
    assertFalse(this.reader.isPackageFiltered(null, configuration));

    configuration.remove("includeFilter");
    configuration.put("excludeFilter", "");
    // only excludeFilter which matches to null/empty packageURL (and non related parameter)
    assertTrue(this.reader.isPackageFiltered(null, configuration));
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.reader.AbstractReader#addComponentToApplicationIfNotFiltered(com.devonfw.tools.solicitor.model.masterdata.Application, com.devonfw.tools.solicitor.model.inventory.ApplicationComponent, java.util.Map, com.devonfw.tools.solicitor.reader.AbstractReader.ReaderStatistics)}.
   *
   * @throws MalformedPackageURLException
   */
  @Test
  void testAddComponentToApplicationIfNotFiltered() throws MalformedPackageURLException {

    Map<String, String> configuration = new HashMap<>();
    configuration.put("includeFilter", "pkg:maven/mygroup/.*");
    PackageURL purl1 = new PackageURL("pkg:maven/mygroup/myartifact@1.2.3");
    PackageURL purl2 = new PackageURL("pkg:maven/mygroup1/myartifact@1.2.3");

    ModelFactory modelFactory = new ModelFactoryImpl();

    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");

    ReaderStatistics statistics = new ReaderStatistics();

    // should not be filtered (i.e. it should be added)
    ApplicationComponent appComponent1 = modelFactory.newApplicationComponent();
    appComponent1.setPackageUrl(purl1);
    assertTrue(
        this.reader.addComponentToApplicationIfNotFiltered(application, appComponent1, configuration, statistics));
    assertEquals(1, application.getApplicationComponents().size());
    assertTrue(appComponent1.getApplication() == application);
    assertEquals(0, statistics.filteredComponentCount);

    // should be filtered (i.e. it should not be added)
    ApplicationComponent appComponent2 = modelFactory.newApplicationComponent();
    appComponent2.setPackageUrl(purl2);
    assertFalse(
        this.reader.addComponentToApplicationIfNotFiltered(application, appComponent2, configuration, statistics));
    assertEquals(1, application.getApplicationComponents().size());
    assertNull(appComponent2.getApplication());
    assertEquals(1, statistics.filteredComponentCount);

    // if PackageURL is null then this shall be handled like an empty string, so filtered out for the given regex
    ApplicationComponent appComponent3 = modelFactory.newApplicationComponent();
    assertFalse(
        this.reader.addComponentToApplicationIfNotFiltered(application, appComponent3, configuration, statistics));
    assertEquals(1, application.getApplicationComponents().size());
    assertFalse(appComponent3.getApplication() == application);
    assertEquals(2, statistics.filteredComponentCount);

    // if PackageURL is null then this shall be handled like an empty string. An empty string Regex should match
    configuration.put("includeFilter", "");
    assertTrue(
        this.reader.addComponentToApplicationIfNotFiltered(application, appComponent3, configuration, statistics));
    assertEquals(2, application.getApplicationComponents().size());
    assertTrue(appComponent3.getApplication() == application);
    assertEquals(2, statistics.filteredComponentCount);

  }

}
