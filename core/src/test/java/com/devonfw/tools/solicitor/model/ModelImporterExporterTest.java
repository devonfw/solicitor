package com.devonfw.tools.solicitor.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * Tests of {@link ModelImporterExporter}.
 *
 */
@SpringBootTest
public class ModelImporterExporterTest {

  @Autowired
  private ModelImporterExporter mie;

  /**
   * Test method for {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#loadModel(java.lang.String)}.
   */
  @Test
  public void testLoadModelVersion1() {

    SolicitorRuntimeException thrown = Assertions.assertThrows(SolicitorRuntimeException.class, () -> {
      this.mie.loadModel("src/test/resources/models/model_version_1.json");
    });

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#loadModel(java.lang.String)}.
   */
  @Test
  public void testLoadModelVersion2() {

    this.mie.loadModel("src/test/resources/models/model_version_2.json");
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#loadModel(java.lang.String)}.
   */
  @Test
  public void testLoadModelVersion3() {

    this.mie.loadModel("src/test/resources/models/model_version_3.json");
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#loadModel(java.lang.String)}.
   */
  @Test
  public void testLoadModelVersion4() {

    this.mie.loadModel("src/test/resources/models/model_version_4.json");
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#loadModel(java.lang.String)}.
   */
  @Test
  public void testLoadModelVersion5() {

    this.mie.loadModel("src/test/resources/models/model_version_5.json");
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#loadModel(java.lang.String)}.
   */
  @Test
  public void testLoadModelVersion6() {

    ModelRoot modelRoot = this.mie.loadModel("src/test/resources/models/model_version_6.json");

    // Assert
    Assertions.assertNotNull(modelRoot);
    Engagement engagement = modelRoot.getEngagement();
    Assertions.assertNotNull(engagement);
    List<Application> applications = engagement.getApplications();
    Assertions.assertFalse(applications.isEmpty());

    Application application = applications.get(0);
    Assertions.assertNotNull(application);

    List<ApplicationComponent> applicationComponents = application.getApplicationComponents();
    Assertions.assertFalse(applicationComponents.isEmpty());

    ApplicationComponent component = applicationComponents.get(0);
    Assertions.assertNotNull(component);

    // Add assertions for specific fields
    Assertions.assertEquals("DA:NO_ISSUES", component.getDataStatus());
    Assertions.assertEquals("", component.getTraceabilityNotes()); // Assuming it's an empty string
    Assertions.assertEquals("https://github.com/qos-ch/logback/archive/refs/tags/logback-1.2.3.zip",
        component.getSourceDownloadUrl());
    Assertions.assertEquals(
        "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar",
        component.getPackageDownloadUrl());
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#saveModel(com.devonfw.tools.solicitor.model.ModelRoot, java.lang.String)}.
   *
   * @throws IOException in case that the temp file could not be created
   */
  @Test
  public void testSaveModel() throws IOException {

    ModelRoot mr = this.mie.loadModel("src/test/resources/models/model_version_6.json");
    File tempFile = File.createTempFile("solicitor_model", "json");
    String fileName = tempFile.getPath();

    this.mie.saveModel(mr, fileName);
    this.mie.loadModel(fileName);
  }

}
