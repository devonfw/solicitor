/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common.packageurl.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLTypException;
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
    throw new SolicitorPackageURLTypException(
        "No applicable SingleKindPackageURLHandler found for type '" + packageURL.getType() + "'");
  }

  @Override
  public String sourceDownloadUrlFor(String packageUrl) {

    PackageURL packageURL = parse(packageUrl);
    return findApplicableSingleKindHandler(packageURL).sourceDownloadUrlFor(packageUrl);
  }

  @Override
  public String packageDownloadUrlFor(String packageUrl) {

    PackageURL packageURL = parse(packageUrl);
    return findApplicableSingleKindHandler(packageURL).packageDownloadUrlFor(packageUrl);
  }

  @Override
  public String pathFor(String packageUrl) {

    PackageURL packageURL = parse(packageUrl);
    return findApplicableSingleKindHandler(packageURL).pathFor(packageUrl);
  }

  @Override
  public String sourceArchiveSuffixFor(String packageUrl) {

    PackageURL packageURL = parse(packageUrl);
    return findApplicableSingleKindHandler(packageURL).sourceArchiveSuffixFor(packageUrl);
  }

}
