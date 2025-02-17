package com.devonfw.tools.solicitor.common.packageurl.impl;

import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLException;
import com.github.packageurl.PackageURL;

/**
 * Abstract base class for {@link SingleKindPackageURLHandler}s.
 */
public abstract class AbstractSingleKindPackageURLHandler extends AbstractPackageURLHandler
    implements SingleKindPackageURLHandler {

  /**
   * The constructor.
   */
  public AbstractSingleKindPackageURLHandler() {

    super();
  }

  @Override
  public String pathFor(PackageURL packageUrl) {

    StringBuffer sb = new StringBuffer();
    sb.append(packageUrl.getScheme()).append("/");
    sb.append(packageUrl.getType()).append("/");
    if (packageUrl.getNamespace() != null) {
      sb.append(packageUrl.getNamespace().replace('.', '/')).append("/");
    }
    sb.append(packageUrl.getName()).append("/");
    sb.append(packageUrl.getVersion());

    String result = sb.toString();
    if (result.contains("..")) {
      throw new IllegalArgumentException("A path constructed for a packageUrl must never contain '..' : " + result);
    }
    return result;
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
   * @throws SolicitorPackageURLException if the kind of PackageURL can not be handled by this instance
   */
  private void checkType(PackageURL packageUrl) {

    if (!canHandle(packageUrl)) {
      throw new SolicitorPackageURLException(
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