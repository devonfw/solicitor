package com.devonfw.tools.solicitor.common.packageurl;

import com.github.packageurl.PackageURL;

/**
 * Provides additional information for / operations on the given package URL.
 */
public interface PackageURLHandler {

  /**
   * Get the URL for downloading the sources of the package referenced by the package URL.
   *
   * @param packageUrl the package URL of the package
   * @return the URL to access the source archive of the package
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   */
  String sourceDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException;

  /**
   * Get the URL for downloading the package referenced by the package URL.
   *
   * @param packageUrl the package URL of the package
   * @return the URL to download the package
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   */
  String packageDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException;

  /**
   * Return the (relative) path to be used when accessing the references package in some tree structure
   *
   * @param packageUrl the package URL of the package
   * @return a relative path, elements delimited by "/"
   */
  String pathFor(PackageURL packageUrl);

  /**
   * Returns the (default) file suffix for the source code archive of the referenced package.
   *
   * @param packageUrl the package URL of the package
   * @return file suffix like e.g. "jar", "tgz", "tar.gz"
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   */
  String sourceArchiveSuffixFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException;

}