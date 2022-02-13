/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

/**
 * Provides access to a {@link Content} objects based on a given URI.
 *
 * @param <C> the type of {@link Content}
 */
public interface ContentProvider<C extends Content> {

  /**
   * Gets the content of the resource given by the URI.
   *
   * @param uri the URI of the resource.
   * @return the content of the resource
   */
  C getContentForUri(String uri);

}
