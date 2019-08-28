/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InMemoryMapWebContentProvider implements WebContentProvider {

    @Autowired
    private ClasspathWebContentProvider classPathWebContentProvider;

    Map<String, String> contentMap = new TreeMap<>();

    public InMemoryMapWebContentProvider() {

        // TODO Auto-generated constructor stub
    }

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
