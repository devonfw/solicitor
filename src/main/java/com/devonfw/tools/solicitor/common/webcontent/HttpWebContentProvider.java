/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpWebContentProvider implements WebContentProvider {

    private static final Logger LOG =
            LoggerFactory.getLogger(HttpWebContentProvider.class);

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
