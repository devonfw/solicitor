package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;

/**
 * Tests for {@link CranPackageURLHandlerImpl}
 *
 */
class CranPackageURLHandlerTests {

  @Test
  void testSourceDownloadUrlFor() throws SolicitorMalformedPackageURLException {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/src/contrib/someprod_4.5.35.tar.gz",
        handler.sourceDownloadUrlFor(PackageURLHelper.fromString("pkg:cran/someprod@4.5.35")));

  }

  @Test
  void testPackageDownloadUrlFor() throws SolicitorMalformedPackageURLException {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/src/contrib/someprod_4.5.35.tar.gz",
        handler.packageDownloadUrlFor(PackageURLHelper.fromString("pkg:cran/someprod@4.5.35")));

  }

  @Test
  void testCanHandle() {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:cran/a@1")));
    assertFalse(handler.canHandle(handler.parse("pkg:crana/a@1")));
  }

  @Test
  void testSourceArchiveSuffixFor() throws SolicitorMalformedPackageURLException {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("tar.gz", handler.sourceArchiveSuffixFor(PackageURLHelper.fromString("pkg:cran/someprod@4.5.35")));
  }

  @Test
  void testPathFor() throws SolicitorMalformedPackageURLException {

    CranPackageURLHandlerImpl handler = new CranPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/cran/someprod/4.5.35", handler.pathFor(PackageURLHelper.fromString("pkg:cran/someprod@4.5.35")));
  }
}
