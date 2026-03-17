/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.impl.ModelRootImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * The {@code ModelImporterExporter} class handles the import and export of the data model. It provides methods for
 * loading a data model from a JSON file, checking model version compatibility, and saving the data model to a file.
 * Additionally, it facilitates the transformation of JSON data into model objects, ensuring proper association between
 * the various components of the data model.
 */
@Component
public class ModelImporterExporter {
  private static final Logger LOG = LoggerFactory.getLogger(ModelImporterExporter.class);

  public static final int LOWEST_SUPPORTED_MODEL_VERSION = 2;

  public static final int LOWEST_VERSION_WITH_PACKAGE_URL = 4;

  public static final int LOWEST_VERSION_WITH_SOURCE_REPO_URL = 5;

  public static final int LOWEST_VERSION_WITH_TEXT_POOL = 6;

  @Autowired
  private ModelFactoryImpl modelFactory;

  /**
   * Loads the data model from a JSON file. The loaded data model is represented by a root object of type
   * {@code ModelRootImpl}.
   *
   * @param filename the name of the file to load the data model from.
   * @return the root object of the loaded data model.
   * @throws SolicitorRuntimeException if there is an issue loading the data model from the file.
   */
  public ModelRootImpl loadModel(String filename) {

    ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      JsonNode root = objectMapper.readTree(new File(filename));
      ModelRootImpl modelRoot = this.modelFactory.newModelRoot();
      int readModelVersion = root.get("modelVersion").asInt();
      checkModelVersion(readModelVersion, modelRoot);
      modelRoot.readModelRootFromJson(root, this.modelFactory, readModelVersion);
      return modelRoot;
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not load internal data model from file '" + filename + "'", e);
    }

  }

  /**
   * Checks if the version of the model to be loaded is supported and compatible with the current model version.
   *
   * @param readModelVersion the model version of the model to be loaded
   * @param currentModelRoot the root object of the current (target) model
   * @throws SolicitorRuntimeException if the model version is unsupported or incompatible.
   */
  private void checkModelVersion(int readModelVersion, ModelRootImpl currentModelRoot) {

    if (readModelVersion < LOWEST_SUPPORTED_MODEL_VERSION || readModelVersion > currentModelRoot.getModelVersion()) {
      throw new SolicitorRuntimeException(
          "Unsupported model version " + readModelVersion + " can not be loaded; version must be in range "
              + LOWEST_SUPPORTED_MODEL_VERSION + " to " + currentModelRoot.getModelVersion() + ".");
    }
  }

  /**
   * Saves the model to a file.
   *
   * @param filename the path/name of the file to save to. If <code>node</code> a filename in the current directory will
   *        be autocreated.
   * @param modelRoot a {@link ModelRoot} object.
   */
  public void saveModel(ModelRoot modelRoot, String filename) {

    String effectiveFilename = (filename != null) ? filename : "solicitor_" + System.currentTimeMillis() + ".json";
    IOHelper.checkAndCreateLocation(effectiveFilename);
    ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      objectMapper.writeValue(new File(effectiveFilename), modelRoot);
    } catch (IOException e) {
      LOG.error("Could not write internal data model to file '{}'", effectiveFilename, e);
    }

  }
}
