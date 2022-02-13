/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link CachingContentProviderBase} which tries to load web content from a (possible static) cache in the classpath.
 */
public class ClasspathContentProvider<C extends Content> extends CachingContentProviderBase<C> {

  private static final Logger LOG = LoggerFactory.getLogger(ClasspathContentProvider.class);

  private String[] cachePaths;

  /**
   * Constructor.
   *
   * @param contentFactory factory for creating instances of C
   * @param nextContentProvider the next {@link ContentProvider} in the chain which will be used if the was no cache hit
   * @param cachePaths the base paths where to look for preconfigured content (e.g. license texts)
   */
  public ClasspathContentProvider(ContentFactory<C> contentFactory, ContentProvider<C> nextContentProvider,
      String[] cachePaths) {

    super(contentFactory, nextContentProvider);
    this.cachePaths = cachePaths;

  }

  /**
   * {@inheritDoc}
   *
   * Points to the folders defined via property {@link #cachePaths} in the classpath.
   */
  @Override
  protected Collection<String> getCacheUrls(String key) {

    List<String> result = new ArrayList<>();
    for (String base : this.cachePaths) {
      result.add(new StringBuilder("classpath:").append(base).append("/").append(key).toString());
    }
    return result;
  }

}
