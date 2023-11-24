package com.devonfw.tools.solicitor.componentinfo;

/**
 * Provides {@link ComponentInfo} for components given by their PackageURL. Subinterfaces further specify if the
 * {@link ComponentInfo} is already curated or not.
 *
 */
public interface ComponentInfoProvider {

  /**
   * Retrieves the component information for a package identified by the given package URL. Returns the data as a
   * {@link ComponentInfo} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @param curationDataSelector identifies which source should be used for the curation data. <code>null</code>
   *        indicates that the default should be used.
   * @return the data for the component. If the bean implementing this interface is deactivated (e.g. via some feature
   *         flag configuration) this method will return <code>null</code>. Otherwise a non-<code>null</code> object
   *         will be refurned. If there is no actual data available for the requested component calling
   *         {@link ComponentInfo#getComponentInfoData()} on the returned object will return <code>null</code>.
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be returned in such a case.
   */
  ComponentInfo getComponentInfo(String packageUrl, String curationDataSelector) throws ComponentInfoAdapterException;

}