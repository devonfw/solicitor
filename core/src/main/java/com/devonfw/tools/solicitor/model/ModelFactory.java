package com.devonfw.tools.solicitor.model;

import java.util.Collection;

import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * An abstract factory for creating objects of the Solicitor data model.
 */
public interface ModelFactory {

  /**
   * Returns the collection of all objects of the model tree starting from the given
   * {@link com.devonfw.tools.solicitor.model.ModelRoot}. All model objects must be instances of the implementation
   * classes for the model objects as used / created by this model factory.
   *
   * @return a collection of {@link Object}s representing all objects of the model
   * @param modelRoot a {@link ModelRoot} object.
   */
  Collection<Object> getAllModelObjects(ModelRoot modelRoot);

  /**
   * Determine the table name for the given {@link AbstractModelObject} subtype.
   *
   * @param modelClass a class name of the {@link AbstractModelObject} subtype
   * @return the table name for storing this to the reporting database
   */
  String determineTableName(Class<? extends AbstractModelObject> modelClass);

  /**
   * Creates a new {@link Application}
   *
   * @return the new instance
   */
  Application newApplication();

  /**
   * Creates a new {@link ApplicationComponent}
   *
   * @return the new instance
   */
  ApplicationComponent newApplicationComponent();

  /**
   * Creates a new {@link Engagement}
   *
   * @return the new instance
   */
  Engagement newEngagement();

  /**
   * Creates a {@link ModelRoot}.
   *
   * @return the new instance
   */
  ModelRoot newModelRoot();

  /**
   * Creates a {@link NormalizedLicense}.
   *
   * @return the new instance
   */
  NormalizedLicense newNormalizedLicense();

  /**
   * Creates a {@link NormalizedLicense}.
   *
   * @param rawLicense a {@link RawLicense} object.
   * @return the new instance
   */
  NormalizedLicense newNormalizedLicense(RawLicense rawLicense);

  /**
   * Creates a new {@link RawLicense}
   *
   * @return the new instance
   */
  RawLicense newRawLicense();

}
