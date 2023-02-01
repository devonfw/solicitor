/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model.impl;

import java.text.DecimalFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base class for all classes implementing model objects.
 */
public abstract class AbstractModelObject {

  private static long idSingleton = 0;

  private static final DecimalFormat integerFormat = new DecimalFormat("000000000");

  /**
   * Concatenates two String arrays.
   *
   * @param first an array
   * @param second an array o
   * @return concatenated array
   */
  public static String[] concatRow(String[] first, String[] second) {

    String[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  private String id;

  /**
   * Constructor.
   */
  public AbstractModelObject() {

    synchronized (integerFormat) {
      this.id = integerFormat.format(idSingleton++);
    }
  }

  /**
   * Returns the parent. To be overridden in subclasses where a parent exits,
   *
   * @return the parent in the model object hierarchy
   */
  @JsonIgnore
  protected Object doGetParent() {

    return null;
  }

  /**
   * Gets an array of Strings which represent the data values contained in this object.
   *
   * @return the value array
   */
  @JsonIgnore
  public abstract String[] getDataElements();

  /**
   * Gets an array of Strings which are the names of the datafields contained in this object.
   *
   * @return the column name array
   */
  @JsonIgnore
  public abstract String[] getHeadElements();

  /**
   * Gets the id of the model object.
   *
   * @return the id
   */
  @JsonIgnore
  public String getId() {

    return this.id;
  }

  /**
   * Gets the parent.
   *
   * @return the parent in the model object hierarchy
   */
  @JsonIgnore
  public final AbstractModelObject getParent() {

    return (AbstractModelObject) doGetParent();
  }

  /**
   * Gets the TextPool for effectively storing license and other texts. Recursively walks up the object tree up to the
   * {@link ModelRootImpl}.
   *
   * @return the text pool of the model
   */
  @JsonIgnore
  protected TextPool getEffectiveTextPool() {

    if (getParent() == null) {
      throw new IllegalStateException("Method might not be called unless object is linked to model tree");
    }
    return getParent().getEffectiveTextPool();

  }

  /**
   * Stores a text in the text pool.
   *
   * @param text the text to store
   * @return the associated key
   * @see TextPool#store(String)
   */
  protected String storeTextInPool(String text) {

    return getEffectiveTextPool().store(text);
  }

  /**
   * Retrieves a text from the text pool
   *
   * @param key the key of the text
   * @return the associated text
   * @see TextPool#retrieve(String)
   */
  protected String retrieveTextFromPool(String key) {

    return getEffectiveTextPool().retrieve(key);
  }
}
