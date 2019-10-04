/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A {@link CachingWebContentProviderBase} which tries to load web content from
 * a (possible static) cache in the classpath.
 */
@Component
public class ClasspathWebContentProvider extends CachingWebContentProviderBase {

    private static final Logger LOG = LoggerFactory.getLogger(ClasspathWebContentProvider.class);

    @Autowired
    private FilesystemCachingWebContentProvider filesystemCachingWebContentProvider;

    /**
     * Constructor.
     */
    public ClasspathWebContentProvider() {

    }

    /**
     * {@inheritDoc}
     * 
     * Points to the folder "licenses" in the classpath.
     */
    @Override
    protected String getCacheUrl(String key) {

        return "classpath:licenses/" + key;
    }

    /**
     * {@inheritDoc}
     * 
     * Delegates next to the {@link FilesystemCachingWebContentProvider}.
     */
    @Override
    public String loadFromNext(String url) {

        String result = filesystemCachingWebContentProvider.getWebContentForUrl(url);
        return result;
    }

}
