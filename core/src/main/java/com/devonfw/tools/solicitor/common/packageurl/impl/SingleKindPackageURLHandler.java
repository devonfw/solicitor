package com.devonfw.tools.solicitor.common.packageurl.impl;

import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.github.packageurl.PackageURL;

/**
 * A PackageURLHandler capable of handling Package URLs of a single kind, like a single type.
 */
public interface SingleKindPackageURLHandler extends PackageURLHandler {

  /**
   * Check if this Instance might handle given {@link PackageURL}.
   *
   * @param packageURL the package URL to check
   * @return <code>true</code> if the handler is capable of handling, <code>false</code> otherwise.
   */
  boolean canHandle(PackageURL packageURL);

}
