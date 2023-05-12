package com.devonfw.tools.solicitor.common.packageurl;

/**
 * Provides additional information for / operations on the given package URL.
 */
public interface PackageURLHandler {

  /**
   * Get the URL for downloading the sources of the package referenced by the package URL.
   *
   * @param packageUrl the package URL of the package (stringified form)
   * @return the URL to access the source archive of the package
   */
  String sourceDownloadUrlFor(String packageUrl);

  /**
   * Get the URL for downloading the package referenced by the package URL.
   *
   * @param packageUrl the package URL of the package (stringified form)
   * @return the URL to download the package
   */
  String packageDownloadUrlFor(String packageUrl);

  /**
   * Return the (relative) path to be used when accessing the references package in some tree structure
   *
   * @param packageUrl the package URL of the package (stringified form)
   * @return a relative path, elements delimited by "/"
   */
  String pathFor(String packageUrl);

  /**
   * Returns the (default) file suffix for the source code archive of the referenced package.
   *
   * @param packageUrl the package URL of the package (stringified form)
   * @return file suffix like e.g. "jar", "tgz", "tar.gz"
   */
  String sourceArchiveSuffixFor(String packageUrl);

}