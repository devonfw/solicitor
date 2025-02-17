package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;

/**
 * Tests for {@link MavenPackageURLHandlerImpl}
 *
 */
class MavenPackageURLHandlerTests {

  @Test
  void testSourceDownloadUrlFor() throws SolicitorMalformedPackageURLException {

    MavenPackageURLHandlerImpl handler = new MavenPackageURLHandlerImpl("http://test/");
    assertEquals("http://test/com/someorg/someprod/4.5.35/someprod-4.5.35-sources.jar",
        handler.sourceDownloadUrlFor(PackageURLHelper.fromString("pkg:maven/com.someorg/someprod@4.5.35")));

  }

  @Test
  void testCanHandle() {

    MavenPackageURLHandlerImpl handler = new MavenPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:maven/a/b@1")));
    assertFalse(handler.canHandle(handler.parse("pkg:maven1/a/b@1")));
  }

  @Test
  void testSourceArchiveSuffixFor() throws SolicitorMalformedPackageURLException {

    MavenPackageURLHandlerImpl handler = new MavenPackageURLHandlerImpl("http://test/");
    assertEquals("jar",
        handler.sourceArchiveSuffixFor(PackageURLHelper.fromString("pkg:maven/com.someorg/someprod@4.5.35")));
  }

  @Test
  void testPathFor() throws SolicitorMalformedPackageURLException {

    MavenPackageURLHandlerImpl handler = new MavenPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/maven/com/someorg/someprod/4.5.35",
        handler.pathFor(PackageURLHelper.fromString("pkg:maven/com.someorg/someprod@4.5.35")));
  }

  @Test
  void testPathForFailsForForgedPackageName() {

    MavenPackageURLHandlerImpl handler = new MavenPackageURLHandlerImpl("http://test/");
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      handler.pathFor(PackageURLHelper.fromString("pkg:maven/com.someorg/some..prod@4.5.35"));
    });
  }
}
