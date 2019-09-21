/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClasspathWebContentProvider extends CachingWebContentProviderBase {

    private static final Logger LOG =
            LoggerFactory.getLogger(ClasspathWebContentProvider.class);

    @Autowired
    private FilesystemCachingWebContentProvider filesystemCachingWebContentProvider;

    public ClasspathWebContentProvider() {

        // TODO Auto-generated constructor stub
    }

    /**
     * @param key
     * @return
     */
    @Override
    protected String getCacheUrl(String key) {

        return "classpath:licenses/" + key;
    }

    @Override
    public String loadFromNext(String url) {

        String result =
                filesystemCachingWebContentProvider.getWebContentForUrl(url);
        return result;
    }

}
