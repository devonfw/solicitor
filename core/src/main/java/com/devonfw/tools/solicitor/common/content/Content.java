/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

/**
 * Base type of any content handled.
 */
public interface Content {

  /**
   * Gets a string representation of the content. To be used for serrialization. This needs to be the inverse operation
   * of {@link ContentFactory#fromString(String)}.
   *
   * @return Content as String
   */
  String asString();
}
