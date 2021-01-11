/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;

/**
 * A {@link WebContentProvider} which tries to load the web content directly via
 * the given URL.
 */
@Component
public class DirectUrlWebContentProvider implements WebContentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DirectUrlWebContentProvider.class);

    @Value("${webcontent.skipdownload}")
    private boolean skipdownload;
    
    /**
     * Constructor.
     */
    public DirectUrlWebContentProvider() {

    }
      
    /**
     * {@inheritDoc}
     *
     * Directly tries to access the given URL via the web.
     */
    @Override
    public WebContentObject getWebContentForUrl(WebContentObject webObj) {
    	String url = webObj.getEffectiveURL();
        URL webContentUrl;
        if (url == null) {
            return webObj;
        }
        if (skipdownload) {
            LOG.info(LogMessages.SKIP_DOWNLOAD.msg(), url);
            return webObj;
        }
        try {
            webContentUrl = new URL(url);
        } catch (MalformedURLException e) {
            LOG.warn("Invalid URL syntax '" + url + "'", e);
            return webObj;
        }

        try (InputStream is = webContentUrl.openConnection().getInputStream(); Scanner s = new Scanner(is)) {
            s.useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            webObj.setContent(result);
            return webObj;
        } catch (IOException e) {
            LOG.warn("Could not retieve content for url '" + url + "'", e);
        }
        return webObj;
    }

}
