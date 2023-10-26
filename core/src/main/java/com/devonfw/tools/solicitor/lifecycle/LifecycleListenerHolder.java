package com.devonfw.tools.solicitor.lifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.model.ModelRoot;

/**
 * Holds all beans of type {@link SolicitorLifecycleListener} and calls all of their respective methods.
 */
@Component
public class LifecycleListenerHolder {

  @Autowired(required = false)
  private SolicitorLifecycleListener[] listeners;

  /**
   * The constructor.
   */
  public LifecycleListenerHolder() {

  }

  /**
   * Calls {@link SolicitorLifecycleListener#modelRootInitialized(ModelRoot)} of all registered
   * {@link SolicitorLifecycleListener}s.
   *
   * @param modelRoot the model data as existing at the beginning of the main processing
   */
  public void modelRootInitialized(ModelRoot modelRoot) {

    if (this.listeners == null) {
      return;
    }
    for (SolicitorLifecycleListener sll : this.listeners) {
      sll.modelRootInitialized(modelRoot);
    }
  }

  /**
   * Calls {@link SolicitorLifecycleListener#afterModelProcessing(ModelRoot)} of all registered
   * {@link SolicitorLifecycleListener}s.
   *
   * @param modelRoot modelRoot the model data as existing after the model processing but before model is (optionally)
   *        saved or writers are called
   */
  public void afterModelProcessing(ModelRoot modelRoot) {

    if (this.listeners == null) {
      return;
    }
    for (SolicitorLifecycleListener sll : this.listeners) {
      sll.afterModelProcessing(modelRoot);
    }
  }

  /**
   * Calls {@link SolicitorLifecycleListener#endOfMainProcessing(ModelRoot)} of all registered
   * {@link SolicitorLifecycleListener}s.
   *
   * @param modelRoot the model data at the end of main processing
   */
  public void endOfMainProcessing(ModelRoot modelRoot) {

    if (this.listeners == null) {
      return;
    }
    for (SolicitorLifecycleListener sll : this.listeners) {
      sll.endOfMainProcessing(modelRoot);
    }
  }

}
