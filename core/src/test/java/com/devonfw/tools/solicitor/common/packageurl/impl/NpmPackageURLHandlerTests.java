package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.ApplicationComponentCoordinates;
import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;

/**
 * Tests for {@link NpmPackageURLHandlerImpl}
 *
 */
class NpmPackageURLHandlerTests {

  @Test
  void testSourceDownloadUrlFor() throws SolicitorMalformedPackageURLException {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/@somenamespace/package/-/package-4.5.35.tgz",
        handler.sourceDownloadUrlFor(PackageURLHelper.fromString("pkg:npm/%40somenamespace/package@4.5.35")));
  }

  @Test
  void testCanHandle() throws SolicitorMalformedPackageURLException {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(PackageURLHelper.fromString("pkg:npm/a@1")));
    assertFalse(handler.canHandle(PackageURLHelper.fromString("pkg:npma/a@1")));
  }

  @Test
  void testSourceArchiveSuffixFor() throws SolicitorMalformedPackageURLException {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertEquals("tgz",
        handler.sourceArchiveSuffixFor(PackageURLHelper.fromString("pkg:npm/%40somenamespace/package@4.5.35")));
  }

  @Test
  void testPathFor() throws SolicitorMalformedPackageURLException {

    AbstractSingleKindPackageURLHandler handler = new NpmPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/npm/@somenamespace/package/4.5.35",
        handler.pathFor(PackageURLHelper.fromString("pkg:npm/%40somenamespace/package@4.5.35")));
  }

  @Test
  void testCoordinatesFor() throws SolicitorMalformedPackageURLException {

    NpmPackageURLHandlerImpl handler = new NpmPackageURLHandlerImpl("http://test/");
    ApplicationComponentCoordinates result = handler
        .coordinatesFor(PackageURLHelper.fromString("pkg:npm/%40somenamespace/package@4.5.35"));
    assertNull(result.getGroupId());
    assertEquals("@somenamespace/package", result.getArtifactId());
    assertEquals("4.5.35", result.getVersion());

    result = handler.coordinatesFor(PackageURLHelper.fromString("pkg:npm/package@4.5.35"));
    assertNull(result.getGroupId());
    assertEquals("package", result.getArtifactId());
    assertEquals("4.5.35", result.getVersion());
  }

}
