package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.LicenseInfo;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCurator;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCuratorImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider;

/**
 * This class contains JUnit test methods for the {@link ScancodeComponentInfoAdapter} class.
 */
class ScancodeComponentInfoAdapterTest {

  // the object under test
  ScancodeComponentInfoAdapter scancodeComponentInfoAdapter;

  UncuratedScancodeComponentInfoProvider uncuratedScancodeComponentInfoProvider;

  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  ComponentInfoCurator componentInfoCuratorImpl;

  SingleFileCurationProvider singleFileCurationProvider;

  @BeforeEach
  public void setup() {

    AllKindsPackageURLHandler packageURLHandler = Mockito.mock(AllKindsPackageURLHandler.class);

    Mockito.when(packageURLHandler.pathFor("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0"))
        .thenReturn("pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0");
    DirectUrlWebContentProvider contentProvider = new DirectUrlWebContentProvider(false);

    this.fileScancodeRawComponentInfoProvider = new FileScancodeRawComponentInfoProvider(contentProvider,
        packageURLHandler);
    this.fileScancodeRawComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

    this.uncuratedScancodeComponentInfoProvider = new UncuratedScancodeComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, packageURLHandler);
    this.uncuratedScancodeComponentInfoProvider.setMinLicensefileNumberOfLines(5);
    this.uncuratedScancodeComponentInfoProvider.setMinLicenseScore(90.0);
    this.uncuratedScancodeComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

    this.singleFileCurationProvider = new SingleFileCurationProvider(packageURLHandler);
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    this.componentInfoCuratorImpl = new ComponentInfoCuratorImpl(this.singleFileCurationProvider,
        this.fileScancodeRawComponentInfoProvider);

    this.scancodeComponentInfoAdapter = new ScancodeComponentInfoAdapter(this.uncuratedScancodeComponentInfoProvider,
        this.componentInfoCuratorImpl);
    this.scancodeComponentInfoAdapter.setFeatureFlag(true);

  }

  /**
   * Test the {@link ScancodeFileAdapter#getComponentInfo(String)} method when such package is known.
   *
   * @throws ComponentInfoAdapterException
   */
  @Test
  public void testGetComponentInfoNaPackage() throws ComponentInfoAdapterException {

    // given

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter
        .getComponentInfo("pkg:maven/com.devonfw.tools/unknown@0.1.0");

    // then
    assertNull(componentInfo);
  }

  /**
   * Test the {@link ScancodeFileAdapter#getComponentInfo(String)} method when no curations are available.
   *
   * @throws ComponentInfoAdapterException
   */
  @Test
  public void testGetComponentInfoWithoutCurations() throws ComponentInfoAdapterException {

    // given
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/nonexisting.yaml");

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter
        .getComponentInfo("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");

    // then
    assertNotNull(componentInfo);
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getNoticeFileContent());
    assertEquals("$PKG_ROOT$/sources/NOTICE.txt", componentInfo.getNoticeFilePath());
    assertEquals(1, componentInfo.getCopyrights().size());
    assertEquals("Copyright 2023 devonfw", componentInfo.getCopyrights().toArray()[0]);
    assertEquals(2, componentInfo.getLicenses().size());

    boolean apacheFound = false;
    boolean unknownFound = false;
    for (LicenseInfo li : componentInfo.getLicenses()) {
      if (li.getSpdxid().equals("Apache-2.0")) {
        Assertions.assertTrue(li.getGivenLicenseText().contains("Unless required by applicable"));
        apacheFound = true;
      }
      if (li.getSpdxid().equals("LicenseRef-scancode-unknown-license-reference")) {
        Assertions.assertNull(li.getGivenLicenseText());
        unknownFound = true;
      }
    }
    assertTrue(apacheFound && unknownFound);

    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getNoticeFileContent());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0.jar",
        componentInfo.getPackageDownloadUrl());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0-sources.jar",
        componentInfo.getSourceDownloadUrl());

  }

  /**
   * Test the {@link ScancodeFileAdapter#getComponentInfo(String)} method when curations are existing.
   *
   * @throws ComponentInfoAdapterException
   */
  @Test
  public void testGetComponentInfoWithCurations() throws ComponentInfoAdapterException {

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter
        .getComponentInfo("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");

    // then
    assertNotNull(componentInfo);
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getNoticeFileContent());
    assertEquals("$PKG_ROOT$/sources/NOTICE.txt", componentInfo.getNoticeFilePath());
    assertEquals(1, componentInfo.getCopyrights().size());
    assertEquals("Copyright (c) 2023 somebody", componentInfo.getCopyrights().toArray()[0]);
    assertEquals(1, componentInfo.getLicenses().size());

    boolean mitFound = false;
    for (LicenseInfo li : componentInfo.getLicenses()) {
      if (li.getSpdxid().equals("MIT")) {
        Assertions.assertEquals("https://some/license/url", li.getLicenseFilePath());
        mitFound = true;
      }
    }
    assertTrue(mitFound);

    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getNoticeFileContent());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0.jar",
        componentInfo.getPackageDownloadUrl());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0-sources.jar",
        componentInfo.getSourceDownloadUrl());
    assertEquals("http://some/url", componentInfo.getSourceRepoUrl());
  }

}
