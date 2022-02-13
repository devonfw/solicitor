/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.content.ContentProvider;

/**
 * A {@link ContentProvider} which tries to load the {@link WebContent} directly via the given URL.
 */
public class DirectUrlWebContentProvider implements ContentProvider<WebContent> {

  private static final Logger LOG = LoggerFactory.getLogger(DirectUrlWebContentProvider.class);

  private boolean skipdownload;

  /**
   * Constructor.
   *
   * @param skipdownload if set to true, then no download will be performed
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
      return new WebContent(null);
    }
    if (this.skipdownload) {
      LOG.info(LogMessages.SKIP_DOWNLOAD.msg(), url);
      return new WebContent(null);
    }
    try {
      webContentUrl = new URL(url);
    } catch (MalformedURLException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Invalid URL syntax '" + url + "'", e);
      }
      LOG.info(LogMessages.COULD_NOT_DOWNLOAD_CONTENT_MALFORMED_URL.msg(), url);
      return new WebContent(null);
    }

    try (InputStream is = webContentUrl.openConnection().getInputStream(); Scanner s = new Scanner(is)) {
      s.useDelimiter("\\A");
      String result = s.hasNext() ? s.next() : "";
      return new WebContent(result);
    } catch (IOException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not retieve content for url '" + url + "'", e);
      }
      LOG.info(LogMessages.COULD_NOT_DOWNLOAD_CONTENT.msg(), url, e.getClass().getSimpleName());
    }
    return new WebContent(null);
  }

}
