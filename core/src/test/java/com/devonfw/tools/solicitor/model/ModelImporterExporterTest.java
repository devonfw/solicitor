package com.devonfw.tools.solicitor.model;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

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

	  SolicitorRuntimeException thrown = Assertions.assertThrows(SolicitorRuntimeException.class, () ->{
		    this.mie.loadModel("src/test/resources/models/model_version_1.json");
	  });
	  
	  //TODO here still Assertions.assertEquals("msg",exception.getMessage()); ?
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
   * Test method for
   * {@link com.devonfw.tools.solicitor.model.ModelImporterExporter#saveModel(com.devonfw.tools.solicitor.model.ModelRoot, java.lang.String)}.
   *
   * @throws IOException in case that the temp file could not be created
   */
  @Test
  public void testSaveModel() throws IOException {

    ModelRoot mr = this.mie.loadModel("src/test/resources/models/model_version_3.json");
    File tempFile = File.createTempFile("solicitor_model", "json");
    String fileName = tempFile.getPath();

    this.mie.saveModel(mr, fileName);
    this.mie.loadModel(fileName);
  }

}
