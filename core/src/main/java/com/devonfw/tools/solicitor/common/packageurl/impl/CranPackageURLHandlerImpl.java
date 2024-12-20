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

  @Override
  protected String doSourceDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    // Fixed path for sources
    sb.append("src/contrib/");
    sb.append(purl.getName()).append("_").append(purl.getVersion()).append(".tar.gz");
    return sb.toString();
  }

  @Override
  protected String doPackageDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    // Fixed path for binaries
    sb.append("bin/windows/contrib/");
    sb.append(purl.getName()).append("_").append(purl.getVersion()).append(".zip");
    return sb.toString();
  }

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
