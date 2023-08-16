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

}