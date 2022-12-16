package com.devonfw.tools.solicitor.lifecycle;

import com.devonfw.tools.solicitor.model.ModelRoot;

/**
 * Abstract base implementation of {@link SolicitorLifecycleListener}. Methods do nothing by default and might be
 * overridden in concrete implementation beans.
 *
 */
public abstract class AbstractSolicitorLifecycleListener implements SolicitorLifecycleListener {

  /**
   * The constructor.
   */
  public AbstractSolicitorLifecycleListener() {

  }

  @Override
  public void modelRootInitialized(ModelRoot modelRoot) {

    // NOOP by default
  }

  @Override
  public void endOfMainProcessing(ModelRoot modelRoot) {

    // NOOP by default
  }

}
