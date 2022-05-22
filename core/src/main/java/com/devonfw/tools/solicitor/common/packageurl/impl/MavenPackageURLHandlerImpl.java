package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.packageurl.PackageURL;

/**
 * Specific handling of packageURLs of "maven" type.
 */
@Component
public class MavenPackageURLHandlerImpl extends AbstractSingleKindPackageURLHandler {

  private String repoBaseUrl;

  /**
   * The constructor.
   *
   * @param repoBaseUrl the repository base url
   */
  @Autowired
  public MavenPackageURLHandlerImpl(@Value("${packageurls.maven.repobaseurl}") String repoBaseUrl) {

    super();
    this.repoBaseUrl = repoBaseUrl;
  }

  @Override
  public boolean canHandle(PackageURL packageURL) {

    return (PackageURL.StandardTypes.MAVEN.equals(packageURL.getType()));
  }

  @Override
  public String doSourceDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    sb.append(purl.getNamespace().replace('.', '/')).append("/");
    sb.append(purl.getName()).append("/");
    sb.append(purl.getVersion()).append("/");
    sb.append(purl.getName()).append("-").append(purl.getVersion());
    sb.append("-sources.jar");

    return sb.toString();
  }

  @Override
  public String doSourceArchiveSuffixFor(PackageURL purl) {

    return "jar";
  }

}
