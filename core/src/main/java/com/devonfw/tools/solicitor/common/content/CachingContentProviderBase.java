/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.devonfw.tools.solicitor.common.UrlInputStreamFactory;

/**
 * Abstract base implementation of {@link ContentProvider}s which first try to load the content from some cache. If they
 * are not able to load the content from the cache they will delegate to some other {@link ContentProvider} for further
 * handling.
 *
 */
public abstract class CachingContentProviderBase<C extends Content> extends AbstractContentProvider<C> {

  private static final Logger LOG = LoggerFactory.getLogger(CachingContentProviderBase.class);

  @Autowired
  private UrlInputStreamFactory urlInputStreamFactory;

  private ContentProvider<C> nextContentProvider;

  // Define the maximum length for filename
  private static final int maxLength = 250;

  /**
   * The Constructor.
   *
   * @param contentFactory factory for creating instances of C
   * @param nextContentProvider the next {@link ContentProvider} in the chain which will be used if the was no cache hit
   */
  public CachingContentProviderBase(ContentFactory<C> contentFactory, ContentProvider<C> nextContentProvider) {

    super(contentFactory);
    this.nextContentProvider = nextContentProvider;

  }

  /**
   * Determine the URL to take in the cache for loading the content.
   *
   * @param key the cache key of the content.
   * @return the URL to take for looking up the content in the cache
   */
  protected abstract Collection<String> getCacheUrls(String key);

  /**
   * Calculate the cache key for the given web content URL.
   *
   * @param url the URL of the web content
   * @return the cache key
   */
  public String getKey(String url) {

    if (url.startsWith("https")) {
      url = url.replace("https", "http");
    }

    // Use the original filename if it's within the maximum length
    if (url.length() <= maxLength) {
      return url;
    }

    // Use the first 40 characters of the original filename
    String firstPart = url.substring(0, Math.min(url.length(), 40));

    // Calculate a hash value of the original filename (e.g., SHA-256)
    String hashPart = calculateHash(url);

    String lastPart = url.substring(url.length() - 40);

    // Combine the parts to create the cache key
    String result = firstPart + hashPart + lastPart;

    // Replace any characters that are not alphanumeric with underscores
    result = result.replaceAll("[^a-zA-Z0-9]", "_");

    return result;
  }

  /**
   * Calculate a hash value for the given string using SHA-256.
   *
   * @param input the input string
   * @return the SHA-256 hash value as a string
   */
  private String calculateHash(String input) {

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = md.digest(input.getBytes());
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        hexString.append(String.format("%02x", b));
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      LOG.error("SHA-256 hashing algorithm not available.", e);
      return "";
    }
  }

  /**
   * {@inheritDoc}
   *
   * Tries to load the web content from the resource found via the URLs returned by {@link #getCacheUrls(String)}. First
   * hit will be returned. If this does not succeed, then delegate further processing to
   * {@link CachingContentProviderBase#loadFromNext(String)}.
   */
  @Override
  public C getContentForUri(String url) {

    String key = getKey(url);
    Collection<String> classPathUrls = getCacheUrls(key);

    for (String classPathUrl : classPathUrls) {
      try (InputStream is = this.urlInputStreamFactory.createInputStreamFor(classPathUrl);
          Scanner s = new Scanner(is)) {
        s.useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        if (LOG.isDebugEnabled()) {
          LOG.debug("Content for url '" + url + "' found at '" + classPathUrl + "'");
        }
        return createContentFromString(result);
      } catch (FileNotFoundException fnfe) {
        LOG.debug("Content for url '" + url + "' NOT found at '" + classPathUrl + "'");
      } catch (IOException e) {
        LOG.debug("Could not retieve content for url '" + url + "' from '" + classPathUrl + "'", e);
      }
    }
    return loadFromNext(url);
  }

  /**
   * Method for loading the requested web content from the next new {@link ContentProvider} defined in the chain.
   *
   * @param url the URL of the requests web content
   * @return the content of the web content given by the URL
   */
  protected C loadFromNext(String url) {

    return this.nextContentProvider.getContentForUri(url);
  }

}
