/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

/**
 * Interface of classes which provide access to the content of web resources
 * given by their URL.
 */
public interface WebContentProvider {

    /**
     * Gets the content of the resource given by the URL.
     *
     * @param url the URL of the resource.
     * @return the content of the resource
     */
    String getWebContentForUrl(String url);

}
