package com.devonfw.tools.solicitor.componentinfo;

import org.springframework.core.annotation.Order;

/**
 * Adapter for reading {@link ComponentInfo} data for a package. There might be different beans in the application
 * implementing this interface. Processing will the be done based on the given {@link Order}. The first non null results
 * will be taken in this case.
 */
public interface ComponentInfoAdapter {

  /**
   * Processing order to be used for the ComponentInfoAdapter. Use in {@link Order} annotation of the implementing
   * spring bean. You might do arithmetics based on this to create higher or lower priority adapters. (higher priority =
   * lower number !)
   */
  public static final int DEFAULT_PRIO = 200;

  /**
   * Retrieves the component information for a package identified by the given package URL. Returns the data as a
   * {@link ComponentInfo} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @return the data for the component. <code>null</code> is returned if no data is available,
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be returned in such a case.
   */
  ComponentInfo getComponentInfo(String packageUrl) throws ComponentInfoAdapterException;

}