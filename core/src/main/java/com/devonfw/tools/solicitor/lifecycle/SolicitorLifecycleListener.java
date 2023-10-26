package com.devonfw.tools.solicitor.lifecycle;

import com.devonfw.tools.solicitor.InventoryProcessor;
import com.devonfw.tools.solicitor.model.ModelRoot;

/**
 * Interface of Beans which will be called at specific points in the Solicitor execution lifecycle.
 */
public interface SolicitorLifecycleListener {

  /**
   * Method to be called at the beginning of the main processing after the model root has been initialized (i.e. the
   * configuration has been read.).
   *
   * @param modelRoot the model data as existing at the beginning of the main processing
   */
  void modelRootInitialized(ModelRoot modelRoot);

  /**
   * Method to be called after the inventory was read (via the readers) but before any further processing.
   *
   * @param modelRoot the model data as existing after the inventory was read via the readers.
   */
  void afterReadingInventory(ModelRoot modelRoot);

  /**
   * Method to be called after the model has been processed, but before the model is potentially saved (and the writers
   * are called).
   *
   * @param modelRoot the model data as existing after the model processing (running {@link InventoryProcessor}s and
   *        completing data). This is before (optionally) saving the model and running the writers to generate the
   *        reports.
   */
  void afterModelProcessing(ModelRoot modelRoot);

  /**
   * Method to be called at the end of main processing.
   *
   * @param modelRoot the model data as existing at the end of the main processing
   */
  void endOfMainProcessing(ModelRoot modelRoot);

}
