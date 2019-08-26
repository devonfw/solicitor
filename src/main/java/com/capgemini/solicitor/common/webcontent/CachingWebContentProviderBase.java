/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.capgemini.solicitor.common.webcontent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;

import com.capgemini.solicitor.common.UrlInputStreamFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CachingWebContentProviderBase
        implements WebContentProvider {

    @Autowired
    private UrlInputStreamFactory urlInputStreamFactory;

    public CachingWebContentProviderBase() {

        // TODO Auto-generated constructor stub
    }

    protected abstract String getCacheUrl(String key);

    public String getKey(String url) {

        String result = url.replaceAll("\\W", "_");
        return result;
    }

    @Override
    public String getWebContentForUrl(String url) {

        String key = getKey(url);
        String classPathUrl = getCacheUrl(key);

        try (InputStream is =
                urlInputStreamFactory.createInputStreamFor(classPathUrl);
                Scanner s = new Scanner(is)) {
            s.useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return result;
        } catch (FileNotFoundException fnfe) {
            LOG.debug("Content for url '" + url + "' not found here");
        } catch (IOException e) {
            LOG.debug("Could not retieve content for url '" + url + "'", e);
        }
        return loadFromNext(url);
    }

    protected abstract String loadFromNext(String url);

}
