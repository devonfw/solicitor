package com.devonfw.tools.solicitor.common.packageurl.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NugetPackageURLHandlerTests {
  @Test
  void testSourceDownloadUrlFor() {

    NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertEquals(null,handler.sourceDownloadUrlFor("pkg:nuget/com.someorg@4.5.35"));
  }
  
  @Test
  void testCanHandle() {

    NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertTrue(handler.canHandle(handler.parse("pkg:nuget/a@1")));
    assertFalse(handler.canHandle(handler.parse("pkg:nuget1/a@1")));
  }
  
  @Test
  void testSourceArchiveSuffixFor() {

  	NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertEquals("nupkg", handler.sourceArchiveSuffixFor("pkg:nuget/com.someorg@4.5.35"));
  }
  
  @Test
  void testPathFor() {

  	NugetPackageURLHandlerImpl handler = new NugetPackageURLHandlerImpl("http://test/");
    assertEquals("pkg/nuget/com.someorg/4.5.35", handler.pathFor("pkg:nuget/com.someorg@4.5.35"));
  }
}
