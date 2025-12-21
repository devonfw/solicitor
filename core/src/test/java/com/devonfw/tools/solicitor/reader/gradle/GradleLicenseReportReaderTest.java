package com.devonfw.tools.solicitor.reader.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

class GradleLicenseReportReaderTest {

  Application application;

  public GradleLicenseReportReaderTest() {

    ModelFactory modelFactory = new ModelFactoryImpl();
    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8",
        "#default#");
    GradleLicenseReportReader gr = new GradleLicenseReportReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("gradle-license-report-json", "src/test/resources/gradleLicenseReport.json", this.application,
        UsagePattern.STATIC_LINKING, false, null, null);
  }

  @Test
  void testGetSupportedTypes() {

    GradleLicenseReportReader cut = new GradleLicenseReportReader();

    assertEquals(1, cut.getSupportedTypes().size());
    assertTrue(cut.getSupportedTypes().contains("gradle-license-report-json"));
  }

  @Test
  void testFindArtifact() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getGroupId().equals("androidx.activity") && //
          ap.getArtifactId().equals("activity") && //
          ap.getVersion().equals("1.2.4")) {
        found = true;
        assertEquals("pkg:maven/androidx.activity/activity@1.2.4", ap.getPackageUrl().toString());
        break;
      }
    }
    assertEquals(3, lapc.size());
    assertTrue(found);
  }

  @Test
  void testFilter() {

    ModelFactory modelFactory = new ModelFactoryImpl();
    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:maven/androidx\\.activity/.*");
    GradleLicenseReportReader gr = new GradleLicenseReportReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("gradle-license-report-json", "src/test/resources/gradleLicenseReport.json", application,
        UsagePattern.STATIC_LINKING, false, null, configuration);

    assertEquals(2, application.getApplicationComponents().size());
  }

  @Test
  void testUsagePatternAndOssModified() {

    ModelFactory modelFactory = new ModelFactoryImpl();
    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8",
        "#default#");
    GradleLicenseReportReader gr = new GradleLicenseReportReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("gradle-license-report-json", "src/test/resources/gradleLicenseReport.json", this.application,
        UsagePattern.DYNAMIC_LINKING, false, null, null);

    assertEquals(UsagePattern.DYNAMIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertFalse(this.application.getApplicationComponents().get(0).isOssModified());

    this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8",
        "#default#");
    gr = new GradleLicenseReportReader();
    gr.setModelFactory(modelFactory);
    gr.setInputStreamFactory(new FileInputStreamFactory());
    gr.readInventory("gradle-license-report-json", "src/test/resources/gradleLicenseReport.json", this.application,
        UsagePattern.STATIC_LINKING, true, null, null);

    assertEquals(UsagePattern.STATIC_LINKING, this.application.getApplicationComponents().get(0).getUsagePattern());
    assertTrue(this.application.getApplicationComponents().get(0).isOssModified());
  }

  @Test
  void testFindLicense() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("istack-commons-runtime")
          && ap.getRawLicenses().get(0).getDeclaredLicense().equals("GPL2 w/ CPE")) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  void testMinimalInformation() {

    List<ApplicationComponent> lapc = this.application.getApplicationComponents();
    boolean found = false;
    for (ApplicationComponent ap : lapc) {
      if (ap.getArtifactId().equals("annotation") && ap.getOssHomepage() == null && ap.getRawLicenses().isEmpty()) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }
}