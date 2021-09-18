/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link ContentProvider} which tries to lookup web content in a local in
 * memory cache.
 */
public class InMemoryMapContentProvider<C extends Content> implements ContentProvider<C> {

    private ContentProvider<C> nextContentProvider;;

    Map<String, C> contentMap = new TreeMap<>();

    /**
     * Constructor.
     */
    public InMemoryMapContentProvider(ContentProvider<C> nextContentProvider) {

        this.nextContentProvider = nextContentProvider;
    }

    /**
     * {@inheritDoc}
     *
     * Tries to find the web content in an in memory map. If not found then it
     * delegates to {@link ClasspathContentProvider}. The result will be stored
     * in the in Memory map for further calls to the same URL.
     */
    @Override
    public C getContentForUri(String url) {

        if (url == null) {
            return null;
        }

        C result;
        if (this.contentMap.containsKey(url)) {
            result = this.contentMap.get(url);
        } else {
            result = this.nextContentProvider.getContentForUri(url);
            this.contentMap.put(url, result);
        }
        return result;
    }

}
