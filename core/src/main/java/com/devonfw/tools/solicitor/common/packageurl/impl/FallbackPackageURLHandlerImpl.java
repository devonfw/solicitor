package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLUnavailableOperationException;
import com.github.packageurl.PackageURL;

/**
 * An {@link AbstractSpecializedPackageURLHandler} which might serve as fallback implementation if no (better fitting)
 * {@link SingleKindPackageURLHandler} exists for the given PackageURL.
 *
 */
@Component
public class FallbackPackageURLHandlerImpl extends AbstractSpecializedPackageURLHandler {

  /**
   * The constructor.
   */
  public FallbackPackageURLHandlerImpl() {

    super();
  }

  @Override
  public String sourceDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    throw new SolicitorPackageURLUnavailableOperationException(
        "No source download URL can be determined for PackageUrl '" + packageUrl + "'");
  }

  @Override
  public String packageDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    throw new SolicitorPackageURLUnavailableOperationException(
        "No package download URL can be determined for PackageUrl '" + packageUrl + "'");
  }

  @Override
  public String sourceArchiveSuffixFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    throw new SolicitorPackageURLUnavailableOperationException(
        "No package suffix can be determined for PackageUrl '" + packageUrl + "'");
  }

}
