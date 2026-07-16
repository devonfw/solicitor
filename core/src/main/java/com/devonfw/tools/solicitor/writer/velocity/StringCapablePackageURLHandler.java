package com.devonfw.tools.solicitor.writer.velocity;

import com.devonfw.tools.solicitor.common.ApplicationComponentCoordinates;
import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLUnavailableOperationException;
import com.github.packageurl.PackageURL;

/**
 * This is an extension of the {@link PackageURLHandler} interface which additionally offers the methods of that
 * interface with the PackageURL given as a String. This ensures compatibility of existing Velocity templates after
 * changing the parameters of the interface methods from String to PackageURL.
 *
 */
public interface StringCapablePackageURLHandler extends PackageURLHandler {

  /**
   * Get the URL for downloading the sources of the package referenced by the package URL.
   *
   * @param packageUrl the package URL of the package
   * @return the URL to access the source archive of the package
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   * @throws SolicitorMalformedPackageURLException if the given String is not a valid representation of a PackageURL.
   */
  String sourceDownloadUrlFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException;

  /**
   * Get the URL for downloading the package referenced by the package URL.
   *
   * @param packageUrl the package URL of the package
   * @return the URL to download the package
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   * @throws SolicitorMalformedPackageURLException if the given String is not a valid representation of a PackageURL.
   */
  String packageDownloadUrlFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException;

  /**
   * Return the (relative) path to be used when accessing the references package in some tree structure
   *
   * @param packageUrl the package URL of the package
   * @return a relative path, elements delimited by "/"
   * @throws SolicitorMalformedPackageURLException if the given String is not a valid representation of a PackageURL.
   */
  String pathFor(String packageUrl) throws SolicitorMalformedPackageURLException;

  /**
   * Returns the (default) file suffix for the source code archive of the referenced package.
   *
   * @param packageUrl the package URL of the package
   * @return file suffix like e.g. "jar", "tgz", "tar.gz"
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   * @throws SolicitorMalformedPackageURLException if the given String is not a valid representation of a PackageURL.
   */
  String sourceArchiveSuffixFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException;

  /**
   * Returns the coordinates of the application component represented by the given package URL.
   *
   * @param packageUrl the package URL of the package
   * @return the coordinates of the application component represented by the given package URL
   * @throws SolicitorPackageURLUnavailableOperationException if the method is unavailable in the implementing
   *         {@link PackageURLHandler} or for the given {@link PackageURL}.
   * @throws SolicitorMalformedPackageURLException if the given String is not a valid representation of a PackageURL.
   */
  ApplicationComponentCoordinates coordinatesFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException;

}
