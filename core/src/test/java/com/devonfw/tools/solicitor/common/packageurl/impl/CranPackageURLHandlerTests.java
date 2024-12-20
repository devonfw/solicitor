package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CranPackageURLHandlerImpl}
 *
 */
class CranPackageURLHandlerTests {

  // Test for source download URL
  @Test
  void testSourceDownloadUrlFor() {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/src/contrib/someprod_4.5.35.tar.gz",
        handler.sourceDownloadUrlFor("pkg:cran/someprod@4.5.35"));

  }

  // Test for can handle
  @Test
  void testCanHandle() {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:cran/a@1")));
    assertFalse(handler.canHandle(handler.parse("pkg:crana/a@1")));
  }

  // Test for source archive suffix
  @Test
  void testSourceArchiveSuffixFor() {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("tar.gz", handler.sourceArchiveSuffixFor("pkg:cran/someprod@4.5.35"));
  }

  // Test for path
  @Test
  void testPathFor() {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/cran/someprod/4.5.35", handler.pathFor("pkg:cran/someprod@4.5.35"));
  }
}
