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
    // Debug-Log hinzufügen, um den ursprünglichen Wert zu prüfen
    System.out.println("Original repoBaseUrl: " + repoBaseUrl);

    // Sicherstellen, dass repoBaseUrl mit '/' endet
    if (!repoBaseUrl.endsWith("/")) {
      repoBaseUrl += "/";
    }
    this.repoBaseUrl = repoBaseUrl;

    // Debug-Log hinzufügen, um den finalen Wert zu prüfen
    System.out.println("Final repoBaseUrl: " + this.repoBaseUrl);
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

    StringBuilder sb = new StringBuilder(this.repoBaseUrl);

    // Debug-Log für Konstruktion
    System.out.println("Constructing Source URL for: " + purl);

    // Sicherstellen, dass der Pfad korrekt ist
    sb.append("src/contrib/");
    sb.append(purl.getName()).append("_").append(purl.getVersion()).append(".tar.gz");

    // Debug-Log für die generierte URL
    System.out.println("Generated SourceDownloadUrl: " + sb.toString());
    return sb.toString();
  }

  @Override
  protected String doPackageDownloadUrlFor(PackageURL purl) {

    StringBuilder sb = new StringBuilder(this.repoBaseUrl);
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
