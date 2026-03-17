package com.devonfw.tools.solicitor.model;

import java.util.Collection;

import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * An abstract factory for creating objects of the Solicitor data model.
 */
public abstract class ModelFactory {

  /**
   * Returns the collection of all objects of the model tree starting from the given
   * {@link com.devonfw.tools.solicitor.model.ModelRoot}. All model objects must be instances of the implementation
   * classes for the model objects as used / created by this model factory.
   *
   * @return a collection of {@link Object}s representing all objects of the model
   * @param modelRoot a {@link ModelRoot} object.
   */
  public abstract Collection<Object> getAllModelObjects(ModelRoot modelRoot);

  /**
   * Creates a new {@link Application}
   *
   * @return the new instance
   */
  public abstract Application newApplication();

  /**
   * Creates a new {@link ApplicationComponent}
   *
   * @return the new instance
   */
  public abstract ApplicationComponent newApplicationComponent();

  /**
   * Creates a new {@link Engagement}
   *
   * @return the new instance
   */
  public abstract Engagement newEngagement();

  /**
   * Creates a {@link ModelRoot}.
   *
   * @return the new instance
   */
  public abstract ModelRoot newModelRoot();

  /**
   * Creates a {@link NormalizedLicense}.
   *
   * @return the new instance
   */
  public abstract NormalizedLicense newNormalizedLicense();

  /**
   * Creates a {@link NormalizedLicense}.
   *
   * @param rawLicense a {@link RawLicense} object.
   * @return the new instance
   */
  public abstract NormalizedLicense newNormalizedLicense(RawLicense rawLicense);

  /**
   * Creates a new {@link RawLicense}
   *
   * @return the new instance
   */
  public abstract RawLicense newRawLicense();

}
