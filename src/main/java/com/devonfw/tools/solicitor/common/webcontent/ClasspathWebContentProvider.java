/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private String[] cachePaths;

    /**
     * Constructor.
     */
    public ClasspathWebContentProvider() {

    }

    /**
     * {@inheritDoc}
     * 
     * Points to the folders defined via property {@link #cachePaths} in the
     * classpath.
     */
    @Override
    protected Collection<String> getCacheUrls(String key) {

        List<String> result = new ArrayList<>();
        for (String base : cachePaths) {
            result.add(new StringBuilder("classpath:").append(base).append("/").append(key).toString());
        }
        return result;
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

    /**
     * This method sets the field <tt>cachePaths</tt>. It defines the base paths
     * where to look for preconfigured license texts.
     *
     * @param cachePaths the new value of the field cachePaths
     */
    @Value("${solicitor.classpath-license-cache-locations}")
    public void setCachePaths(String[] cachePaths) {

        this.cachePaths = cachePaths;
    }

}
