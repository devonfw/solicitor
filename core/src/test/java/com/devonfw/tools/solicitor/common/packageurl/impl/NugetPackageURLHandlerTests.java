package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;

public class NugetPackageURLHandlerTests {
  @Test
  void testSourceDownloadUrlFor() throws SolicitorMalformedPackageURLException {

    NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertEquals(null, handler.sourceDownloadUrlFor(PackageURLHelper.fromString("pkg:nuget/com.someorg@4.5.35")));
  }

  @Test
  void testCanHandle() {

    NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:nuget/a@1")));
    assertFalse(handler.canHandle(handler.parse("pkg:nuget1/a@1")));
  }

  @Test
  void testSourceArchiveSuffixFor() throws SolicitorMalformedPackageURLException {

    NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertEquals("nupkg", handler.sourceArchiveSuffixFor(PackageURLHelper.fromString("pkg:nuget/com.someorg@4.5.35")));
  }

  @Test
  void testPathFor() throws SolicitorMalformedPackageURLException {

    NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/nuget/com.someorg/4.5.35",
        handler.pathFor(PackageURLHelper.fromString("pkg:nuget/com.someorg@4.5.35")));
  }
}
