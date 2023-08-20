package com.devonfw.tools.solicitor.componentinfo;

import org.springframework.core.annotation.Order;

/**
 * Adapter for reading {@link ComponentInfo} data for a package. This basically is a {@link ComponentInfoProvider} with
 * the additional semantic that the {@link ComponentInfo} is "ready to use" i.e. any possible curations have been
 * applied. There might be different beans in the application implementing this interface. Processing will the be done
 * based on the given {@link Order}. The first non null results will be taken in this case.
 */
public interface ComponentInfoAdapter extends ComponentInfoProvider {

  /**
   * Processing order to be used for the ComponentInfoAdapter. Use in {@link Order} annotation of the implementing
   * spring bean. You might do arithmetics based on this to create higher or lower priority adapters. (higher priority =
   * lower number !)
   */
  public static final int DEFAULT_PRIO = 200;

}