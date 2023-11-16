package com.devonfw.tools.solicitor.common.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link CachingContentProviderBase#getKey(String)} method.
 */
class CachingContentProviderBaseTest {

  // Dummy implementation of CachingContentProviderBase for testing
  static class TestCachingContentProvider extends CachingContentProviderBase<Content> {
    public TestCachingContentProvider() {

      super(null, null); // ContentFactory and ContentProvider not needed for this test
    }

    @Override
    protected Collection<String> getCacheUrls(String key) {

      return null;
    }
  }

  @Test
  void shouldGenerateCorrectKeyForUrlOfLength250() {

    TestCachingContentProvider cachingContentProvider = new TestCachingContentProvider();

    // Create a URL of length 250 (classical logic should be used)
    String longUrl250 = "http://example.com/clear/and/concise/url/for/testing/purposes/with/exactly/250/characters/in/total/including/letters/numbers/special/characters/as/appropriate/for/clarity/this/is/a/very/long/url/the/maximum/filename/length/just/to/reach/to/length/250";
    String longResult250 = cachingContentProvider.getKey(longUrl250);
    assertTrue(longResult250.length() <= 250, // Use the constant value directly exact 250 test, lenght
        "Modified filename length exceeds the maximum for URL of length 250");
  }

  void shouldGenerateCorrectKeyForUrlOfLength251() {

    TestCachingContentProvider cachingContentProvider = new TestCachingContentProvider();

    // Create a URL of length 251 (new approach should be used)
    String longUrl251 = "http://example.com/clear/and/concise/url/for/testing/purposes/with/exactly/251/characters/in/total/including/letters/numbers/special/characters/as/appropriate/for/clarity/this/is/a/very/long/url/the/maximum/filenames/length/just/to/reach/to/length/251";
    String longResult251 = cachingContentProvider.getKey(longUrl251);
    assertEquals(144, longResult251.length(), "Modified filename length is incorrect for URL of length 251");
  }

}
