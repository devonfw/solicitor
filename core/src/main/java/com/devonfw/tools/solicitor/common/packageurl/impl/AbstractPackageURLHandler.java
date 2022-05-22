package com.devonfw.tools.solicitor.common.packageurl.impl;

import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLException;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;

/**
 * Abstract base class for {@link PackageURLHandler}s.
 *
 */
public abstract class AbstractPackageURLHandler implements PackageURLHandler {

  /**
   * The constructor.
   */
  public AbstractPackageURLHandler() {

  }

  /**
   * Parse a String to a {@link PackageURL}.
   *
   * @param packageURLAsString the string to be parsed
   * @return the corresponding Package URL
   * @throws SolicitorPackageURLException if the string could not be parsed
   */
  protected PackageURL parse(String packageURLAsString) {

    try {
      return new PackageURL(packageURLAsString);
    } catch (MalformedPackageURLException e) {
      throw new SolicitorPackageURLException("Not a valid PackageURL: '" + packageURLAsString + "'", e);
    }
  }

}
