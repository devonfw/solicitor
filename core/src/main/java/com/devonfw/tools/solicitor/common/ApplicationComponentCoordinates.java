package com.devonfw.tools.solicitor.common;

/**
 * This class represents the coordinates of an application component.
 */
public class ApplicationComponentCoordinates {

  private final String groupId;

  private final String artifactId;

  private final String version;

  /**
   * Creates a new instance of {@link ApplicationComponentCoordinates} with the given values.
   *
   * @param groupId the groupId
   * @param artifactId the artifactId
   * @param version the version
   */
  public ApplicationComponentCoordinates(String groupId, String artifactId, String version) {

    super();
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  /**
   * This method gets the field <code>groupId</code>.
   *
   * @return the field groupId
   */
  public String getGroupId() {

    return this.groupId;
  }

  /**
   * This method gets the field <code>artifactId</code>.
   *
   * @return the field artifactId
   */
  public String getArtifactId() {

    return this.artifactId;
  }

  /**
   * This method gets the field <code>version</code>.
   *
   * @return the field version
   */
  public String getVersion() {

    return this.version;
  }

}
