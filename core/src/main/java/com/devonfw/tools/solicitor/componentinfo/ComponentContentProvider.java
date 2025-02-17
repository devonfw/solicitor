package com.devonfw.tools.solicitor.componentinfo;

import com.github.packageurl.PackageURL;

/**
 * Allows retrieving content from a (sub)path within the component given by its PackageURL.
 */
public interface ComponentContentProvider {

  /**
   * Required (custom) prefix of the uri.
   */
  public static final String PKG_CONTENT_SCHEMA_PREFIX = "pkgcontent:/";

  /**
   * Retrieves content from the (sub)path of a component given by the PackageUrl.
   *
   * @param packageUrl the packageUrl which references the component
   * @param contentUri an uri giving the path to the content (relative to the root directory of the component)
   * @return the found data. <code>null</code> if the uri does not start with {@link #PKG_CONTENT_SCHEMA_PREFIX} or
   *         nothing was found.
   */
  String retrieveContent(PackageURL packageUrl, String contentUri);

}