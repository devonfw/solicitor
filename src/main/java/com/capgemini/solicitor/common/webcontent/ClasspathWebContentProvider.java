/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.capgemini.solicitor.common.webcontent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ClasspathWebContentProvider extends CachingWebContentProviderBase {

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
