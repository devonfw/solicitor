/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLException;
import com.github.packageurl.PackageURL;

/**
 * An {@link AllKindsPackageURLHandler} which will delegate handling to the first {@link SingleKindPackageURLHandler}
 * which is found to be capable of handling the given package URL.
 */
@Component
public class DelegatingPackageURLHandlerImpl extends AbstractPackageURLHandler implements AllKindsPackageURLHandler {

  @Autowired
  SingleKindPackageURLHandler[] singleKindHandlers;

  private SingleKindPackageURLHandler findApplicableSingleKindHandler(PackageURL packageURL) {

    for (SingleKindPackageURLHandler singleKindHandler : this.singleKindHandlers) {
      if (singleKindHandler.canHandle(packageURL)) {
        return singleKindHandler;
      }
    }
    throw new SolicitorPackageURLException(
        "No applicable SingleKindPackageURLHandler found for type '" + packageURL.getType() + "'");
  }

  @Override
  public String sourceDownloadUrlFor(PackageURL packageUrl) {

    return findApplicableSingleKindHandler(packageUrl).sourceDownloadUrlFor(packageUrl);
  }

  @Override
  public String packageDownloadUrlFor(PackageURL packageUrl) {

    return findApplicableSingleKindHandler(packageUrl).packageDownloadUrlFor(packageUrl);
  }

  @Override
  public String pathFor(PackageURL packageUrl) {

    return findApplicableSingleKindHandler(packageUrl).pathFor(packageUrl);
  }

  @Override
  public String sourceArchiveSuffixFor(PackageURL packageUrl) {

    return findApplicableSingleKindHandler(packageUrl).sourceArchiveSuffixFor(packageUrl);
  }

}
