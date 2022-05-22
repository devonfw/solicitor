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
  public String pathFor(String packageUrl) {

    StringBuffer sb = new StringBuffer();
    PackageURL purl = parse(packageUrl);
    sb.append(purl.getScheme()).append("/");
    sb.append(purl.getType()).append("/");
    if (purl.getNamespace() != null) {
      sb.append(purl.getNamespace().replace('.', '/')).append("/");
    }
    sb.append(purl.getName()).append("/");
    sb.append(purl.getVersion());

    return sb.toString();
  }

  @Override
  public String sourceDownloadUrlFor(String packageUrl) {

    PackageURL purl = parsePackageURLAndCheckType(packageUrl);
    return doSourceDownloadUrlFor(purl);
  }

  /**
   * Returns the source download URL. To be implemented in subclasses.
   *
   * @param purl the package url
   * @return the source download URL
   */
  protected abstract String doSourceDownloadUrlFor(PackageURL purl);

  /**
   * Parse the given String into a {@link PackageURL} and assures if it can be handled.
   *
   * @param packageUrl the package url as a string
   * @return the parsed package URL
   * @throws SolicitorPackageURLException if either parsing failed or the kind can not be handled by this instance
   */
  private PackageURL parsePackageURLAndCheckType(String packageUrl) {

    PackageURL purl = parse(packageUrl);
    if (!canHandle(purl)) {
      throw new SolicitorPackageURLException(
          "This kind of package URL ('" + packageUrl + "') cannot be handled by this SingleKindPackareURLHandler");
    }
    return purl;
  }

  @Override
  public String sourceArchiveSuffixFor(String packageUrl) {

    PackageURL purl = parsePackageURLAndCheckType(packageUrl);
    return doSourceArchiveSuffixFor(purl);
  }

  /**
   * Returns the source archive suffix for the kind of Package URL handled.
   *
   * @param purl the package url
   * @return the suffix
   */
  protected abstract String doSourceArchiveSuffixFor(PackageURL purl);

}