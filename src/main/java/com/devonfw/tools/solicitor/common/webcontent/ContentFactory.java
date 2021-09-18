/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

/**
 * Abstract factory for {@link Content} of type C
 */
public interface ContentFactory<C extends Content> {

    /**
     * Create content from a given string.
     *
     * @param string the string representation of the content
     * @return the content object
     */
    C fromString(String string);

}
