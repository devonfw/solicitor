package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.ApplicationComponentCoordinates;
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
  protected String doPackageDownloadUrlFor(PackageURL purl) {

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

  @Override
  public ApplicationComponentCoordinates coordinatesFor(PackageURL packageUrl) {

    if (packageUrl.getNamespace() == null) {
      return new ApplicationComponentCoordinates(null, packageUrl.getName(), packageUrl.getVersion());
    } else {
      // due to historical reasons the namespace is not mapped to groupId in Solicitor, but instead is part of the
      // artifactId. So we need to merge namespace and name here. From todays standpoint this does not follow the
      // standard mapping of packageURL to coordinates, but we need to do this for npm to not break existing usage of
      // npm package URLs in Solicitor. In the future we should consider to change this and map namespace to groupId and
      // name to artifactId, but that would be a breaking change.
      return new ApplicationComponentCoordinates(null, packageUrl.getNamespace() + "/" + packageUrl.getName(),
          packageUrl.getVersion());
    }
  }

}
