package com.devonfw.tools.solicitor.componentinfo.scancode;

import com.devonfw.tools.solicitor.componentinfo.ComponentContentProvider;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;

/**
 * Provider for {@link ScancodeRawComponentInfo}
 *
 */
public interface ScancodeRawComponentInfoPovider extends ComponentContentProvider {

  /**
   * Retrieve the {@link ScancodeRawComponentInfo} for the package given by its PackageURL.
   *
   * @param packageUrl the identifier for the package
   * @return the raw data based on scancode and supplemental data. <code>null</code> if no data is available.
   * @throws ComponentInfoAdapterException is something unexpected happens
   */
  ScancodeRawComponentInfo readScancodeData(String packageUrl) throws ComponentInfoAdapterException;

  /**
   * Creates a pkgcontent-URI (see {@link ComponentContentProvider}) from the relative local file path.
   *
   * @param packageUrl the PackageUrl of the component. Implementations might use this reference to the component to
   *        check within the components stored data how the requested uri should be built.
   * @param path the path referencing file content
   *
   * @return a pkgContent URI which might be used for retrieving the content vis
   *         {@link ComponentContentProvider#retrieveContent(String, String)}
   */
  String pkgContentUriFromPath(String packageUrl, String path);

  /**
   * Checks if the argument seems to be a (relative) path pointing to some content within the package.
   *
   * @param packageUrl the PackageUrl of the component. Implementations might use this reference to the component to
   *        check within the components stored data if the given path is valid.
   * @param path the path to check
   *
   * @return <code>true</code> if the seems to be a correct path, <code>false</code> otherwise.
   */
  boolean isLocalContentPath(String packageUrl, String path);

}