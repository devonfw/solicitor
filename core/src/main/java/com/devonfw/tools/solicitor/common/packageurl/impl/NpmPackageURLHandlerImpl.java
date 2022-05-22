package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.packageurl.PackageURL;

/**
 * Specific handling of packageURLs of "npm" type.
 */
@Component
public class NpmPackageURLHandlerImpl extends AbstractSingleKindPackageURLHandler {

  private String repoBaseUrl;

  /**
   * The constructor.
   *
   * @param repoBaseUrl the repository base url
   */
  @Autowired
  public NpmPackageURLHandlerImpl(@Value("${packageurls.npm.repobaseurl}") String repoBaseUrl) {

    super();
    this.repoBaseUrl = repoBaseUrl;
  }

  @Override
  public boolean canHandle(PackageURL packageURL) {

    return (PackageURL.StandardTypes.NPM.equals(packageURL.getType()));
  }

  @Override
  protected String doSourceDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    if (purl.getNamespace() != null) {
      sb.append(purl.getNamespace()).append("/");
    }
    sb.append(purl.getName()).append("/-/");
    sb.append(purl.getName()).append("-");
    sb.append(purl.getVersion()).append(".tgz");

    return sb.toString();
  }

  @Override
  protected String doSourceArchiveSuffixFor(PackageURL packageURL) {

    return "tgz";
  }

}
