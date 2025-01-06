package com.devonfw.tools.solicitor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import com.github.packageurl.PackageURLBuilder;

/**
 * Static helper methods for handling of {@link PackageURL}s.
 *
 * @see <a href="https://github.com/package-url/purl-spec">Package URL spec</a>
 */
public class PackageURLHelper {

  private static final Logger LOG = LoggerFactory.getLogger(PackageURLHelper.class);

  /**
   * Private Constructor prevents initialization.
   */
  private PackageURLHelper() {

  }

  /**
   * Create a {@link PackageURL} for the given NPM package name (including version), e.g.
   * <code>@namespace/package@1.0.0</code>.
   *
   * @param npmPackageNameWithVersion package name and version
   * @return the created PackageURL
   */
  public static PackageURL fromNpmPackageNameWithVersion(String npmPackageNameWithVersion) {

    int versionBoundry = npmPackageNameWithVersion.lastIndexOf("@");
    if (versionBoundry <= 0 || versionBoundry >= npmPackageNameWithVersion.length() - 1) {
      LOG.error(LogMessages.NOT_A_VALID_NPM_PACKAGE_IDENTIFIER.msg(), npmPackageNameWithVersion);
      throw new SolicitorRuntimeException("Invalid NPM package identifier (version info not available)");
    }

    String packageName = npmPackageNameWithVersion.substring(0, versionBoundry);
    String version = npmPackageNameWithVersion.substring(versionBoundry + 1, npmPackageNameWithVersion.length());

    return fromNpmPackageNameAndVersion(packageName, version);

  }

  /**
   * Create a {@link PackageURL} for the given NPM package name , e.g. <code>@namespace/package</code> and version.
   *
   * @param packageName package name
   * @param version the version
   * @return the created PackageURL
   */
  public static PackageURL fromNpmPackageNameAndVersion(String packageName, String version) {

    PackageURLBuilder builder = PackageURLBuilder.aPackageURL().withType(PackageURL.StandardTypes.NPM);
    builder.withVersion(version);

    String[] parts = packageName.split("/");
    switch (parts.length) {
      case 1:
        builder.withName(parts[0]);
        break;
      case 2:
        builder.withNamespace(parts[0]);
        builder.withName(parts[1]);
        break;
      default:
        LOG.error(LogMessages.NOT_A_VALID_NPM_PACKAGE_NAME.msg(), packageName);
        throw new SolicitorRuntimeException("Invalid NPM package identifier");
    }

    try {
      return builder.build();
    } catch (MalformedPackageURLException e) {
      throw new SolicitorRuntimeException("The given NPM package identifier '" + packageName + "@" + version
          + "' could not be converted to a package URL", e);
    }
  }

  /**
   * Create a {@link PackageURL} for the Maven coordinates.
   *
   * @param groupId the maven groupId
   * @param artifactId the maven artifactId
   * @param version the version
   * @return the created Package URL
   */
  public static PackageURL fromMavenCoordinates(String groupId, String artifactId, String version) {

    PackageURLBuilder builder = PackageURLBuilder.aPackageURL().withType(PackageURL.StandardTypes.MAVEN);
    builder.withNamespace(groupId);
    builder.withName(artifactId);
    builder.withVersion(version);

    try {
      return builder.build();
    } catch (MalformedPackageURLException e) {
      throw new SolicitorRuntimeException("The given Maven coordinates '" + groupId + "'/'" + artifactId + "'/'"
          + version + "' could not be converted to a package URL", e);
    }
  }

  /**
   * Create a {@link PackageURL} for the PyPI coordinates.
   *
   * @param name the package name
   * @param version the version
   * @return the created Package URL
   */
  public static PackageURL fromPyPICoordinates(String name, String version) {

    PackageURLBuilder builder = PackageURLBuilder.aPackageURL().withType(PackageURL.StandardTypes.PYPI);
    builder.withName(name);
    builder.withVersion(version);

    try {
      return builder.build();
    } catch (MalformedPackageURLException e) {
      throw new SolicitorRuntimeException(
          "The given PyPI coordinates '" + name + "'/'" + version + "' could not be converted to a package URL", e);
    }
  }

  /**
   * Create a {@link PackageURL} for the CRAN coordinates.
   *
   * @param name the package name
   * @param version the version
   * @return the created Package URL
   */
  public static PackageURL fromCranCoordinates(String name, String version) {

    PackageURLBuilder builder = PackageURLBuilder.aPackageURL().withType("cran");
    builder.withName(name);
    builder.withVersion(version);

    try {
      return builder.build();
    } catch (MalformedPackageURLException e) {
      throw new SolicitorRuntimeException(
          "The given CRAN coordinates '" + name + "'/'" + version + "' could not be converted to a package URL", e);
    }
  }
}
