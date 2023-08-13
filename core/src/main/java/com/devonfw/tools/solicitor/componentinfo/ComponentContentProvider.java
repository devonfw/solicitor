package com.devonfw.tools.solicitor.componentinfo;

/**
 * Allows retrieving content from a (sub)path within the component given by its PackageURL.
 */
public interface ComponentContentProvider {

  /**
   * Required prefix of the path.
   */
  public static final String PATH_PREFIX = "$PKG_ROOT$/";

  /**
   * Retrieves content from the (sub)path of a component given by the PackageUrl.
   *
   * @param packageUrl the packageUrl which references the component
   * @param path the path of the data within the component. The path needs to have the prefix {@value #PATH_PREFIX}.
   * @return the found data
   */
  String retrieveContent(String packageUrl, String path);

}