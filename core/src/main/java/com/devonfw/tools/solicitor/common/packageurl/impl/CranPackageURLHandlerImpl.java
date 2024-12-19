package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.packageurl.PackageURL;

/**
 * Specific handling of packageURLs of "cran" type.
 */
@Component
public class CranPackageURLHandlerImpl extends AbstractSingleKindPackageURLHandler {
  private String repoBaseUrl;

  @Autowired
  public CranPackageURLHandlerImpl(@Value("${packageurls.cran.repobaseurl}") String repoBaseUrl) {

    super();
    this.repoBaseUrl = repoBaseUrl;
  }

  // @Override
  // public boolean canHandle(PackageURL packageURL) {

  // return (PackageURL.StandardTypes.NPM.equals(packageURL.getType()));
  // }
  /**
   * Determines if this handler can handle the given package URL.
   *
   * @param packageURL the package URL to check.
   * @return true if the handler can handle the URL, false otherwise.
   */
  @Override
  public boolean canHandle(PackageURL packageURL) {

    return "cran".equalsIgnoreCase(packageURL.getType());
  }

  /**
   * Generates the download URL for the source archive of a CRAN package.
   *
   * @param purl the package URL.
   * @return the source download URL.
   */
  @Override
  protected String doSourceDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    sb.append(purl.getName()).append("/src/contrib/");
    sb.append(purl.getName()).append("_").append(purl.getVersion()).append(".tar.gz");
    return sb.toString();
  }

  /**
   * Generates the download URL for the binary package for a CRAN package.
   *
   * @param purl the package URL.
   * @return the binary package download URL.
   */
  @Override
  protected String doPackageDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    sb.append(purl.getName()).append("/bin/windows/contrib/");
    sb.append(purl.getName()).append("_").append(purl.getVersion()).append(".zip");
    return sb.toString();
  }

  /*
   *
   * @Override protected String doPackageDownloadUrlFor(PackageURL purl) { // URL to download binary package from CRAN
   * return cranRepoBaseUrl + "/bin/" + purl.getName() + "_" + purl.getVersion() + ".tar.gz"; }
   */

  /**
   * Returns the suffix for source archive files for CRAN packages.
   *
   * @param purl the package URL.
   * @return the suffix for the source archive (".tar.gz").
   */
  @Override
  protected String doSourceArchiveSuffixFor(PackageURL purl) {

    return "tar.gz";
  }

}
