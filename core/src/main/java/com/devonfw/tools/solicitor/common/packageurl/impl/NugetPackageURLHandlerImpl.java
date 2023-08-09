package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.packageurl.PackageURL;
@Component
public class NugetPackageURLHandlerImpl extends AbstractSingleKindPackageURLHandler {
	private String repoBaseUrl;									// https://www.nuget.org/api/v2/package/

  /**
   * The constructor.
   *
   * @param repoBaseUrl the repository base url
   */
  @Autowired
  public NugetPackageURLHandlerImpl(@Value("${packageurls.nuget.repobaseurl}") String repoBaseUrl) {

    super();
    this.repoBaseUrl = repoBaseUrl;
  }
  
  @Override
  public boolean canHandle(PackageURL packageURL) {

  	return (PackageURL.StandardTypes.NUGET.equals(packageURL.getType()));
  }


  // Nuget does not have a standardized API to fetch the source code
  // This needs to be handled by another component (LicenseUrlGuesser?)
  @Override
  public String doSourceDownloadUrlFor(PackageURL purl) {

	  return null;
  }

	// https://www.nuget.org/api/v2/package/{packageID}/{packageVersion}
	// Example: https://www.nuget.org/api/v2/package/Castle.Core/4.4.1
	@Override
	public String doPackageDownloadUrlFor(PackageURL purl) {
	
	  StringBuffer sb = new StringBuffer(this.repoBaseUrl);				// https://www.nuget.org/api/v2/package/
	  sb.append(purl.getName()).append("/");											// {packageID}/
	  sb.append(purl.getVersion());																// {packageVersion}

	  return sb.toString();
	}

  @Override
  public String doSourceArchiveSuffixFor(PackageURL packageURL) {

    return "nupkg";
  }

}
