/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

/**
 * TODO ohecker: This type ...
 *
 * @author <a href="TODO@sdm.de">TODO</a>
 * @version $Revision$
 * @param <C>
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
