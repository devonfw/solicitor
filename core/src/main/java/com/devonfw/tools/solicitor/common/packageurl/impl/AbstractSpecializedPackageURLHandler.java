package com.devonfw.tools.solicitor.common.packageurl.impl;

import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.github.packageurl.PackageURL;

/**
 * Abstract base class for {@link PackageURLHandler}s which can be used to implement {@link PackageURLHandler}s for
 * concrete PackageURL types or groups of types.
 *
 */
public abstract class AbstractSpecializedPackageURLHandler implements PackageURLHandler {

  /**
   * The constructor.
   */
  public AbstractSpecializedPackageURLHandler() {

  }

  @Override
  public String pathFor(PackageURL packageUrl) {
  
    StringBuffer sb = new StringBuffer();
    sb.append(packageUrl.getScheme()).append("/");
    sb.append(packageUrl.getType()).append("/");
    if (packageUrl.getNamespace() != null) {
      sb.append(packageUrl.getNamespace().replace('.', '/')).append("/");
    }
    sb.append(packageUrl.getName()).append("/");
    sb.append(packageUrl.getVersion());
  
    String result = sb.toString();
    if (result.contains("..")) {
      throw new IllegalArgumentException("A path constructed for a packageUrl must never contain '..' : " + result);
    }
    return result;
  }

}
