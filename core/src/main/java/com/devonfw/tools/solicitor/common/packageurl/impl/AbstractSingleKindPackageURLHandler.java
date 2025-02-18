package com.devonfw.tools.solicitor.common.packageurl.impl;

import com.github.packageurl.PackageURL;

/**
 * Abstract base class for {@link SingleKindPackageURLHandler}s.
 */
public abstract class AbstractSingleKindPackageURLHandler extends AbstractSpecializedPackageURLHandler
    implements SingleKindPackageURLHandler {

  /**
   * The constructor.
   */
  public AbstractSingleKindPackageURLHandler() {

    super();
  }

  @Override
  public String sourceDownloadUrlFor(PackageURL packageUrl) {

    checkType(packageUrl);
    return doSourceDownloadUrlFor(packageUrl);
  }

  /**
   * Returns the source download URL. To be implemented in subclasses.
   *
   * @param purl the package url
   * @return the source download URL
   */
  protected abstract String doSourceDownloadUrlFor(PackageURL purl);

  @Override
  public String packageDownloadUrlFor(PackageURL packageUrl) {

    checkType(packageUrl);
    return doPackageDownloadUrlFor(packageUrl);
  }

  /**
   * Returns the package download URL. To be implemented in subclasses.
   *
   * @param purl the package url
   * @return the package download URL
   */
  protected abstract String doPackageDownloadUrlFor(PackageURL purl);

  /**
   * Check if the type of {@link PackageURL} can be handled by this handler.
   *
   * @param packageUrl the package url as a string
   * @throws IllegalArgumentException if the kind of PackageURL can not be handled by this instance
   */
  private void checkType(PackageURL packageUrl) {

    if (!canHandle(packageUrl)) {
      throw new IllegalArgumentException(
          "This kind of package URL ('" + packageUrl + "') cannot be handled by this SingleKindPackareURLHandler");
    }
  }

  @Override
  public String sourceArchiveSuffixFor(PackageURL packageUrl) {

    checkType(packageUrl);
    return doSourceArchiveSuffixFor(packageUrl);
  }

  /**
   * Returns the source archive suffix for the kind of Package URL handled.
   *
   * @param purl the package url
   * @return the suffix
   */
  protected abstract String doSourceArchiveSuffixFor(PackageURL purl);

}