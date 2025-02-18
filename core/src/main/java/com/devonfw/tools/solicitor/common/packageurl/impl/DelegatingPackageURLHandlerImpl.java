/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLUnavailableOperationException;
import com.github.packageurl.PackageURL;

/**
 * An {@link AllKindsPackageURLHandler} which will delegate handling to the first {@link SingleKindPackageURLHandler}
 * which is found to be capable of handling the given package URL. If no such handler is found then the method will
 * delegate to the {@link FallbackPackageURLHandlerImpl}.
 */
@Component
public class DelegatingPackageURLHandlerImpl implements AllKindsPackageURLHandler {

  @Autowired
  private SingleKindPackageURLHandler[] singleKindHandlers;

  @Autowired
  private FallbackPackageURLHandlerImpl fallbackHandler;

  private PackageURLHandler findApplicableDelegateHandler(PackageURL packageURL) {

    for (SingleKindPackageURLHandler singleKindHandler : this.singleKindHandlers) {
      if (singleKindHandler.canHandle(packageURL)) {
        return singleKindHandler;
      }
    }
    return this.fallbackHandler;
  }

  @Override
  public String sourceDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    return findApplicableDelegateHandler(packageUrl).sourceDownloadUrlFor(packageUrl);
  }

  @Override
  public String packageDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    return findApplicableDelegateHandler(packageUrl).packageDownloadUrlFor(packageUrl);
  }

  @Override
  public String pathFor(PackageURL packageUrl) {

    return findApplicableDelegateHandler(packageUrl).pathFor(packageUrl);
  }

  @Override
  public String sourceArchiveSuffixFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    return findApplicableDelegateHandler(packageUrl).sourceArchiveSuffixFor(packageUrl);
  }

}
