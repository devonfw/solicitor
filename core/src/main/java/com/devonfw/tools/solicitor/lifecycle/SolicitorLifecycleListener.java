package com.devonfw.tools.solicitor.lifecycle;

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
   * Method to be called at before reports are written.
   * 
   * @param modelRoot the model data as existing at the beginning of the main processing
   */
  void beforeWriterProcessing(ModelRoot modelRoot);

  /**
   * Method to be called at the end of main processing.
   *
   * @param modelRoot the model data as existing at the end of the main processing
   */
  void endOfMainProcessing(ModelRoot modelRoot);

}
