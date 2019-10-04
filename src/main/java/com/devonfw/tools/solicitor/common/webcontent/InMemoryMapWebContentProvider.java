/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A {@link WebContentProvider} which tries to lookup web content in a local in
 * memory cache.
 */
@Component
public class InMemoryMapWebContentProvider implements WebContentProvider {

    @Autowired
    private ClasspathWebContentProvider classPathWebContentProvider;

    Map<String, String> contentMap = new TreeMap<>();

    /**
     * Constructor.
     */
    public InMemoryMapWebContentProvider() {

    }

    /**
     * {@inheritDoc}
     * 
     * Tries to find the web content in an in memory map. If not found then it
     * delegates to {@link ClasspathWebContentProvider}. The result will be
     * stored in the in Memory map for further calls to the same URL.
     */
    @Override
    public String getWebContentForUrl(String url) {

        if (url == null) {
            return null;
        }

        String result;
        if (contentMap.containsKey(url)) {
            result = contentMap.get(url);
        } else {
            result = classPathWebContentProvider.getWebContentForUrl(url);
            contentMap.put(url, result);
        }
        return result;
    }

}
