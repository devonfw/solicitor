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

  /**
   * The constructor.
   *
   * @param repoBaseUrl the repository base url
   */
  @Autowired
  public CranPackageURLHandlerImpl(@Value("${packageurls.cran.repobaseurl}") String repoBaseUrl) {

    super();
    // Sicherstellen, dass repoBaseUrl mit einem "/" endet
    if (!repoBaseUrl.endsWith("/")) {
      repoBaseUrl += "/";
    }
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

  /**
   * Returns the source download URL for CRAN packages.
   *
   * @param purl the package URL.
   * @return the source download URL.
   */
  @Override
  protected String doSourceDownloadUrlFor(PackageURL purl) {

    StringBuilder sb = new StringBuilder(this.repoBaseUrl);
    sb.append("src/contrib/");
    sb.append(purl.getName());
    sb.append("_").append(purl.getVersion());
    sb.append(".tar.gz");
    return sb.toString();
  }

  /**
   * Returns the package download URL for CRAN packages.
   *
   * @param purl the package URL.
   * @return the package download URL. This will return the archive of sources (same as
   *         {@link #doSourceDownloadUrlFor(PackageURL)}. There are pre-compiled binaries available on the server, but
   *         those are OS and version specific, so no universal binary.
   */
  @Override
  protected String doPackageDownloadUrlFor(PackageURL purl) {

    StringBuilder sb = new StringBuilder(this.repoBaseUrl);
    sb.append("src/contrib/");
    sb.append(purl.getName());
    sb.append("_").append(purl.getVersion());
    sb.append(".tar.gz");
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
