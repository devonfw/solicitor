/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.capgemini.solicitor.common;

/**
 * A data row in a {@link DataTable}.
 *
 */
public interface DataTableRow extends Cloneable {

    /**
     * Gets a field value by its index.
     * 
     * @param index the index of the field
     * @return the field value
     */
    Object getValueByIndex(int index);

    /**
     * Gets a field value by its name.
     * 
     * @param fieldName the name of the field
     * @return the field value
     */
    Object get(String fieldName);

    /**
     * The clone method.
     * 
     * @return the cloned object.
     * @see Object#clone()
     */
    DataTableRow clone();
}
