/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    String urlWithoutLines;
    int startOfLineInfo = url.indexOf("#L");
    String lineInfo = null;
    if (startOfLineInfo >= 0) {
      lineInfo = url.substring(startOfLineInfo, url.length());
      url = url.substring(0, startOfLineInfo);
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

      return new WebContent(possiblyExtractLines(result, lineInfo));
    } catch (IOException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not retieve content for url '" + url + "'", e);
      }
      LOG.info(LogMessages.COULD_NOT_DOWNLOAD_CONTENT.msg(), url, e.getClass().getSimpleName());
    }
    return new WebContent(null);
  }

  /**
   * @param input
   * @param lineInfo
   * @return
   */
  private String possiblyExtractLines(String input, String lineInfo) {

    if (lineInfo == null) {
      return input;
    }
    Pattern pattern = Pattern.compile("#L(\\d+)(-L(\\d+))?");
    Matcher matcher = pattern.matcher(lineInfo);
    if (matcher.find()) {
      int startLine = Integer.parseInt(matcher.group(1));
      int endLine = Integer.parseInt(matcher.group(3) != null ? matcher.group(3) : matcher.group(1));
      String[] splitted = input.split("\\n");
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < splitted.length; i++) {
        if (i + 1 >= startLine && i + 1 <= endLine) {
          result.append(splitted[i]).append("\n");
        }
      }
      return result.toString();
    } else {
      throw new IllegalStateException("Regex did not find line info - this seems to be a bug.");
    }
  }

}
