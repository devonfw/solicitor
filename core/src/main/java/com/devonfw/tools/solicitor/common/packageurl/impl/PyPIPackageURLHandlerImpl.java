package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.packageurl.PackageURL;

/**
 * Specific handling of packageURLs of "pypi" type.
 */
@Component
public class PyPIPackageURLHandlerImpl extends AbstractSingleKindPackageURLHandler {

  private String repoBaseUrl;

  /**
   * The constructor.
   *
   * @param repoBaseUrl the repository base url
   */
  @Autowired
  public PyPIPackageURLHandlerImpl(@Value("${packageurls.pypi.repobaseurl}") String repoBaseUrl) {

    super();
    this.repoBaseUrl = repoBaseUrl;
  }

  @Override
  public boolean canHandle(PackageURL packageURL) {

    return (PackageURL.StandardTypes.PYPI.equals(packageURL.getType()));
  }

  @Override
  public String doSourceDownloadUrlFor(PackageURL purl) {

    StringBuffer sb = new StringBuffer(this.repoBaseUrl);
    sb.append("source/");
    sb.append(purl.getName().substring(0, 1)).append("/");
    sb.append(purl.getName()).append("/");
    sb.append(purl.getName()).append("-");
    sb.append(purl.getVersion()).append(".tar.gz");

    return sb.toString();
  }

  @Override
  public String doSourceArchiveSuffixFor(PackageURL purl) {

    return "tar.gz";
  }

}
