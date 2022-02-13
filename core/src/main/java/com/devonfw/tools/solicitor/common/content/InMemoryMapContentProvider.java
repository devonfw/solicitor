/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link ContentProvider} which tries to lookup {@link Content} in a local in memory cache.
 */
public class InMemoryMapContentProvider<C extends Content> extends AbstractContentProvider<C> {

  private ContentProvider<C> nextContentProvider;;

  Map<String, C> contentMap = new TreeMap<>();

  /**
   * Constructor.
   *
   * @param contentFactory factory for creating instances of C
   * @param nextContentProvider the next {@link ContentProvider} in the chain which will be used if the was no cache hit
   */
  public InMemoryMapContentProvider(ContentFactory<C> contentFactory, ContentProvider<C> nextContentProvider) {

    super(contentFactory);

    this.nextContentProvider = nextContentProvider;
  }

  /**
   * {@inheritDoc}
   *
   * Tries to find the web content in an in memory map. If not found then it delegates to
   * {@link ClasspathContentProvider}. The result will be stored in the in Memory map for further calls to the same URL.
   */
  @Override
  public C getContentForUri(String url) {

    if (url == null || url.isEmpty()) {
      return createEmptyContent();
    }

    C result;
    if (this.contentMap.containsKey(url)) {
      result = this.contentMap.get(url);
    } else {
      result = this.nextContentProvider.getContentForUri(url);
      this.contentMap.put(url, result);
    }
    return result;
  }

}
