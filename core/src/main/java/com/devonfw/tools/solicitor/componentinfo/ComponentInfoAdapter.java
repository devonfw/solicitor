package com.devonfw.tools.solicitor.componentinfo;

/**
 * Adapter for reading {@link ComponentInfo} data for a package.
 */
public interface ComponentInfoAdapter {

  /**
   * Retrieves the component information for a package identified by the given package URL. Returns the data as a
   * {@link ComponentInfo} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @return the data for the componente. <code>null</code> is returned if no data is available,
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  ComponentInfo getComponentInfo(String packageUrl) throws ComponentInfoAdapterException;

}