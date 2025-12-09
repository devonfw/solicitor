/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

/**
 * Abstract factory for {@link Content} of type C
 *
 * @param <C> type of {@link Content} created by this factory
 */
public interface ContentFactory<C extends Content> {

  /**
   * Create content from a given string.
   *
   * @param string the string representation of the content
   * @return the content object
   */
  C fromString(String string);

  /**
   * Create an empty content object.
   *
   * @return the empty content object
   */
  C emptyContent();
}
