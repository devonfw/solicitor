/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

/**
 * Contains helper methods for working with maven version info.
 */
public class MavenVersionHelper {

  /**
   * Private constructor. Only provides static methods.
   */
  private MavenVersionHelper() {

  }

  /**
   * Check if the given maven version string matches the expected range.
   * 
   * @param version a version string in maven format
   * @param expectedVersionRange a version range spec in maven format, see
   * @return <code>true</code> if the version is in the range, <code>false</code> otherwise
   * @see VersionRange#containsVersion(org.apache.maven.artifact.versioning.ArtifactVersion)
   */
  public static boolean checkVersionRange(final String version, final String expectedVersionRange) {

    DefaultArtifactVersion solicitorVersion = new DefaultArtifactVersion(version);
    VersionRange allowedRange;
    try {
      allowedRange = VersionRange.createFromVersionSpec(expectedVersionRange);
    } catch (InvalidVersionSpecificationException e) {
      throw new SolicitorRuntimeException(e);
    }
    return allowedRange.containsVersion(solicitorVersion);
  }

}
