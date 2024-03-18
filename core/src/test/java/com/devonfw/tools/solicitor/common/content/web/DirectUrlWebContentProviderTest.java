package com.devonfw.tools.solicitor.common.content.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link DirectUrlWebContentProvider}
 *
 */
class DirectUrlWebContentProviderTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider#isSupportedUrl(java.lang.String)}.
   */
  @Test
  void testIsSupportedUrl() {

    DirectUrlWebContentProvider provider = new DirectUrlWebContentProvider(false);

    assertTrue(provider.isSupportedUrl("http:foo"));
    assertTrue(provider.isSupportedUrl("https:foo"));
    assertTrue(provider.isSupportedUrl("jar:http:foo"));
    assertTrue(provider.isSupportedUrl("jar:https:foo"));

    assertTrue(provider.isSupportedUrl("hTTp:foo"));

    assertFalse(provider.isSupportedUrl("file:some.file"));
    assertFalse(provider.isSupportedUrl("file:http:foo"));

  }

}
