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

    String result = url.replaceAll("\\W", "_");

    // Check if the filename length exceeds the maximum length
    if (result.length() <= maxLength) {
      return result; // If it's within the limit, use it as is.
    } else {
      // If the filename length is too long, create a modified filename.
      String prefix = result.substring(0, 40);
      String suffix = result.substring(result.length() - 40);

      // Calculate a hash value of the original filename (e.g., using SHA-256)
      String hash = generateHash(result);

      // Combine the prefix, hash, and suffix to create a unique filename
      String modifiedFilename = prefix + hash + suffix;

      // Make sure the modified filename does not exceed the maximum length
      if (modifiedFilename.length() > maxLength) {
        modifiedFilename = modifiedFilename.substring(0, maxLength);
      }

      return modifiedFilename;
    }
  }

  /**
   * Generates a SHA-256 hash of the input string.
   *
   * @param input The input string to be hashed.
   * @return A hexadecimal string representation of the SHA-256 hash.
   * 
   */
  private String generateHash(String input) {

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(input.getBytes());
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
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