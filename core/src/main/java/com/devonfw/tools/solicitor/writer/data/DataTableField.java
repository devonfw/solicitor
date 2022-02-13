/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.writer.data;

/**
 * Container for a field within the {@link DataTableRow} of a {@link DataTable}. This might also hold new and old value
 * in case a diff between two {@link DataTable}s has been done.
 */
public interface DataTableField {

  /**
   * Possible states of the difference of the both values stored in this object.
   */
  public static enum FieldDiffStatus {
    /**
     * no diff has been done or no old values known
     */
    UNAVAILABLE,

    /**
     * value has changed
     */
    CHANGED,

    /**
     * value is unchanged
     */
    UNCHANGED
  }

  /**
   * Gets the difference status of the field represented by this object
   *
   * @return the difference status
   */
  FieldDiffStatus getDiffStatus();

  /**
   * Gets the old value. The existence of an old value is optional and only is used when the difference of the results
   * between two executions of Solicitor is calculated.
   *
   * @return the old value stored in this object; <code>null</code> if no old value exists
   */
  Object getOldValue();

  /**
   * Gets the (current) value.
   *
   * @return the value stored in this object
   */
  Object getValue();

  /**
   * Sets the old value. Will automatically compute the resulting difference status.
   *
   * @param oldValue the old value
   */
  void setOldValue(Object oldValue);

  /**
   * {@inheritDoc}
   * 
   * Needs to return the value stored in this field as templating engines (esp. Velocity) call the {@link #toString()}
   * method to determine the value to use of output.
   */
  @Override
  String toString();

}
