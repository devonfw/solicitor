/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model.impl;

import java.util.Date;

import com.devonfw.tools.solicitor.model.ModelImporterExporter;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.impl.masterdata.EngagementImpl;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation class of the root of the Solicitor data model.
 */
public class ModelRootImpl extends AbstractModelObject implements ModelRoot {

  private static final int DEFAULT_MODEL_VERSION = 8;

  private String executionTime;

  private int modelVersion;

  private String solicitorVersion;

  private String solicitorGitHash;

  private String solicitorBuilddate;

  private String extensionArtifactId;

  private String extensionVersion;

  private String extensionGitHash;

  private String extensionBuilddate;

  private String reportingGroup;

  private Engagement engagement;

  private TextPool textPool;

  /**
   * Constructor.
   */
  public ModelRootImpl() {

    super();
    this.modelVersion = DEFAULT_MODEL_VERSION;
    this.executionTime = (new Date()).toString();
    this.textPool = new TextPoolImpl();

  }

  /** {@inheritDoc} */
  @Override
  public String[] getDataElements() {

    return new String[] { this.executionTime, Integer.toString(this.modelVersion), this.solicitorVersion,
    this.solicitorGitHash, this.solicitorBuilddate, this.extensionArtifactId, this.extensionVersion,
    this.extensionGitHash, this.extensionBuilddate, this.reportingGroup };
  }

  /** {@inheritDoc} */
  @Override
  public Engagement getEngagement() {

    return this.engagement;
  }

  /** {@inheritDoc} */
  @Override
  public String getExecutionTime() {

    return this.executionTime;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadElements() {

    return new String[] { "executionTime", "modelVersion", "solicitorVersion", "solicitorGitHash", "solicitorBuilddate",
    "extensionArtifactId", "extensionVersion", "extensionGitHash", "extensionBuilddate", "reportingGroup" };
  }

  /** {@inheritDoc} */
  @Override
  public int getModelVersion() {

    return this.modelVersion;
  }

  /** {@inheritDoc} */
  @Override
  public String getSolicitorBuilddate() {

    return this.solicitorBuilddate;
  }

  /** {@inheritDoc} */
  @Override
  public String getSolicitorGitHash() {

    return this.solicitorGitHash;
  }

  /** {@inheritDoc} */
  @Override
  public String getSolicitorVersion() {

    return this.solicitorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public String getExtensionArtifactId() {

    return this.extensionArtifactId;
  }

  /** {@inheritDoc} */
  @Override
  public String getExtensionVersion() {

    return this.extensionVersion;
  }

  /** {@inheritDoc} */
  @Override
  public String getExtensionGitHash() {

    return this.extensionGitHash;
  }

  /** {@inheritDoc} */
  @Override
  public String getExtensionBuilddate() {

    return this.extensionBuilddate;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public String getReportingGroup() {

    return this.reportingGroup;
  }

  /** {@inheritDoc} */
  @Override
  public void setEngagement(Engagement engagement) {

    this.engagement = engagement;
  }

  /** {@inheritDoc} */
  @Override
  public void setExecutionTime(String executionTime) {

    this.executionTime = executionTime;
  }

  /** {@inheritDoc} */
  @Override
  public void setModelVersion(int modelVersion) {

    this.modelVersion = modelVersion;
  }

  /** {@inheritDoc} */
  @Override
  public void setSolicitorBuilddate(String solicitorBuilddate) {

    this.solicitorBuilddate = solicitorBuilddate;
  }

  /** {@inheritDoc} */
  @Override
  public void setSolicitorGitHash(String solicitorGitHash) {

    this.solicitorGitHash = solicitorGitHash;
  }

  /** {@inheritDoc} */
  @Override
  public void setSolicitorVersion(String solicitorVersion) {

    this.solicitorVersion = solicitorVersion;
  }

  /** {@inheritDoc} */
  @Override
  public void setExtensionArtifactId(String extensionArtifactId) {

    this.extensionArtifactId = extensionArtifactId;
  }

  /** {@inheritDoc} */
  @Override
  public void setExtensionVersion(String extensionVersion) {

    this.extensionVersion = extensionVersion;
  }

  /** {@inheritDoc} */
  @Override
  public void setExtensionGitHash(String extensionGitHash) {

    this.extensionGitHash = extensionGitHash;
  }

  /** {@inheritDoc} */
  @Override
  public void setExtensionBuilddate(String extensionBuilddate) {

    this.extensionBuilddate = extensionBuilddate;
  }

  /** {@inheritDoc} */
  @Override
  public void setReportingGroup(String reportingGroup) {

    this.reportingGroup = reportingGroup;
  }

  /**
   * @return textPool
   */
  @Override
  public TextPool getEffectiveTextPool() {

    return this.textPool;
  }

  /**
   * @return textPool
   */
  public TextPool getTextPool() {

    return this.textPool;
  }

  @Override
  public void completeData() {

    this.engagement.completeData();
  }

  /**
   * Read the data of the ModelRoot from a JsonNode.
   *
   * @param root the JsonNode containing the data of the ModelRoot
   * @param modelFactory the ModelFactoryImpl to create the EngagementImpl object for the engagement of the ModelRoot
   * @param readModelVersion the version of the model to read, which can be used to handle differences in the model
   */
  public void readModelRootFromJson(JsonNode root, ModelFactoryImpl modelFactory, int readModelVersion) {

    setExecutionTime(root.get("executionTime").asText());

    setSolicitorVersion(root.get("solicitorVersion").asText());

    setSolicitorGitHash(root.get("solicitorGitHash").asText());

    setSolicitorBuilddate(root.get("solicitorBuilddate").asText());

    setExtensionArtifactId(root.get("extensionArtifactId").asText());

    setExtensionVersion(root.get("extensionVersion").asText());

    setExtensionGitHash(root.get("extensionGitHash").asText());

    setExtensionBuilddate(root.get("extensionBuilddate").asText());

    JsonNode engagementNode = root.get("engagement");
    EngagementImpl engagement = modelFactory.newEngagement();
    engagement.setModelRoot(this);
    engagement.readEngagementFromJsonNode(engagementNode, modelFactory, readModelVersion);

    JsonNode textPoolNode = null;
    if (readModelVersion >= ModelImporterExporter.LOWEST_VERSION_WITH_TEXT_POOL) {
      textPoolNode = root.get("textPool");
      JsonNode dataMapNode = textPoolNode.get("dataMap");

      TextPool textPool = getTextPool();
      for (JsonNode singleEntryValue : dataMapNode) {
        // only store values; keys will be reconstructed based on values
        textPool.store(singleEntryValue.asText());
      }
    } else {
      // previous versions do not contain license texts, so complete the data now
      completeData();
    }
  }

}
