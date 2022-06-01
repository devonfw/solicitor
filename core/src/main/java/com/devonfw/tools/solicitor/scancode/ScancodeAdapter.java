package com.devonfw.tools.solicitor.scancode;

/**
 * Adapter for reading Scancode information for a package, applying any given curations and returning the information as
 * a {@link ComponentScancodeInfos} object.
 */
public interface ScancodeAdapter {

  /**
   * Retrieves the Scancode information and curations for a package identified by the given package URL. Returns the
   * data as a {@link ComponentScancodeInfos} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @return the data derived from the scancode results after applying any defined curations. <code>null</code> is
   *         returned if no data is available,
   * @throws ScancodeException if there was an exception when reading the data. In case that there is no data available
   *         no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  ComponentScancodeInfos getComponentScancodeInfos(String packageUrl) throws ScancodeException;

}