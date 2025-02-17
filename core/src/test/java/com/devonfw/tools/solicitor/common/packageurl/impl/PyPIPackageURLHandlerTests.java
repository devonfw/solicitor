package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;

/**
 * Tests for {@link PyPIPackageURLHandlerImpl}
 *
 */
class PyPIPackageURLHandlerTests {

  @Test
  void testSourceDownloadUrlFor() throws SolicitorMalformedPackageURLException {

    PyPIPackageURLHandlerImpl handler = new PyPIPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/source/s/somepackage/somepackage-1.2.3.tar.gz",
        handler.sourceDownloadUrlFor(PackageURLHelper.fromString("pkg:pypi/somepackage@1.2.3")));
  }

  @Test
  void testCanHandle() {

    PyPIPackageURLHandlerImpl handler = new PyPIPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:pypi/somepackage@1.2.3")));
    assertFalse(handler.canHandle(handler.parse("pkg:pypi1/somepackage@1.2.3")));
  }

  @Test
  void testSourceArchiveSuffixFor() throws SolicitorMalformedPackageURLException {

    PyPIPackageURLHandlerImpl handler = new PyPIPackageURLHandlerImpl("http://test/");
    assertEquals("tar.gz", handler.sourceArchiveSuffixFor(PackageURLHelper.fromString("pkg:pypi/somepackage@1.2.3")));
  }

  @Test
  void testPathFor() throws SolicitorMalformedPackageURLException {

    PyPIPackageURLHandlerImpl handler = new PyPIPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/pypi/somepackage/1.2.3",
        handler.pathFor(PackageURLHelper.fromString("pkg:pypi/somepackage@1.2.3")));
  }
}
