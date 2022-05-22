package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NpmPackageURLHandlerImpl}
 *
 */
class NpmPackageURLHandlerTests {

  @Test
  void testSourceDownloadUrlFor() {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/@somenamespace/package/-/package-4.5.35.tgz",
        handler.sourceDownloadUrlFor("pkg:npm/%40somenamespace/package@4.5.35"));
  }

  @Test
  void testCanHandle() {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:npm/a@1")));
    assertFalse(handler.canHandle(handler.parse("pkg:npma/a@1")));
  }

  @Test
  void testSourceArchiveSuffixFor() {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertEquals("tgz", handler.sourceArchiveSuffixFor("pkg:npm/%40somenamespace/package@4.5.35"));
  }

  @Test
  void testPathFor() {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/npm/@somenamespace/package/4.5.35", handler.pathFor("pkg:npm/%40somenamespace/package@4.5.35"));
  }
}
