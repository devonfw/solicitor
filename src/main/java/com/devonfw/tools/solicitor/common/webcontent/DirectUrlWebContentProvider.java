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

import com.devonfw.tools.solicitor.common.LogMessages;

/**
 * A {@link ContentProvider<WebContent>} which tries to load the web content
 * directly via the given URL.
 */
public class DirectUrlWebContentProvider implements ContentProvider<WebContent> {

    private static final Logger LOG = LoggerFactory.getLogger(DirectUrlWebContentProvider.class);

    private boolean skipdownload;

    /**
     * Constructor.
     */
    public DirectUrlWebContentProvider(boolean skipdownload) {

        this.skipdownload = skipdownload;

    }

    /**
     * {@inheritDoc}
     *
     * Directly tries to access the given URL via the web.
     */
    @Override
    public WebContent getContentForUri(String url) {

        URL webContentUrl;
        if (url == null) {
            return null;
        }
        if (this.skipdownload) {
            LOG.info(LogMessages.SKIP_DOWNLOAD.msg(), url);
            return null;
        }
        try {
            webContentUrl = new URL(url);
        } catch (MalformedURLException e) {
            LOG.warn("Invalid URL syntax '" + url + "'", e);
            return null;
        }

        try (InputStream is = webContentUrl.openConnection().getInputStream(); Scanner s = new Scanner(is)) {
            s.useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return new WebContent(result);
        } catch (IOException e) {
            LOG.warn("Could not retieve content for url '" + url + "'", e);
        }
        return null;
    }

}
