/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.capgemini.solicitor.common.webcontent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpWebContentProvider implements WebContentProvider {

    public HttpWebContentProvider() {

        // TODO Auto-generated constructor stub
    }

    @Override
    public String getWebContentForUrl(String url) {

        URL webContentUrl;
        if (url == null) {
            return null;
        }
        try {
            webContentUrl = new URL(url);
        } catch (MalformedURLException e) {
            LOG.warn("Invalid URL syntax '" + url + "'", e);
            return null;
        }

        try (InputStream is = webContentUrl.openConnection().getInputStream();
                Scanner s = new Scanner(is)) {
            s.useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return result;
        } catch (IOException e) {
            LOG.warn("Could not retieve content for url '" + url + "'", e);
        }
        return null;
    }

}
