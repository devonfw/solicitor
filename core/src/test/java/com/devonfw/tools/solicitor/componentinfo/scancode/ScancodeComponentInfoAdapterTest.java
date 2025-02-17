package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.LicenseInfo;
import com.devonfw.tools.solicitor.componentinfo.SelectorCurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCurator;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCuratorImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationProvider;
import com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider;
import com.github.packageurl.PackageURL;

/**
 * This class contains JUnit test methods for the {@link ScancodeComponentInfoAdapter} class.
 */
class ScancodeComponentInfoAdapterTest {

  PackageURL testPackageURL;

  PackageURL unknownTestPackageURL;

  // the object under test
  ScancodeComponentInfoAdapter scancodeComponentInfoAdapter;

  FilteredScancodeV31ComponentInfoProvider filteredScancodeComponentInfoProvider31;

  FilteredScancodeV32ComponentInfoProvider filteredScancodeComponentInfoProvider32;

  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  MultiversionFilteredScancodeComponentInfoProvider multiversionFilteredScancodeComponentInfoProvider;

  ComponentInfoCurator componentInfoCuratorImpl;

  SingleFileCurationProvider singleFileCurationProvider;

  @BeforeEach
  public void setup() throws SolicitorMalformedPackageURLException {

    this.testPackageURL = PackageURLHelper
        .fromString("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");
    this.unknownTestPackageURL = PackageURLHelper.fromString("pkg:maven/com.devonfw.tools/unknown@0.1.0");

    AllKindsPackageURLHandler packageURLHandler = Mockito.mock(AllKindsPackageURLHandler.class);

    Mockito.when(packageURLHandler.pathFor(this.testPackageURL))
        .thenReturn("pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0");

    Mockito.when(packageURLHandler.pathFor(this.unknownTestPackageURL))
        .thenReturn("pkg/maven/com/devonfw/tools/unknown/0.1.0");

    this.fileScancodeRawComponentInfoProvider = new FileScancodeRawComponentInfoProvider(packageURLHandler);
    this.fileScancodeRawComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

    this.singleFileCurationProvider = new SingleFileCurationProvider(packageURLHandler);
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    this.filteredScancodeComponentInfoProvider31 = new FilteredScancodeV31ComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, this.singleFileCurationProvider);
    this.filteredScancodeComponentInfoProvider31.setMinLicensefileNumberOfLines(5);
    this.filteredScancodeComponentInfoProvider31.setMinLicenseScore(90.0);

    this.filteredScancodeComponentInfoProvider32 = new FilteredScancodeV32ComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, this.singleFileCurationProvider);
    this.filteredScancodeComponentInfoProvider32.setMinLicensefileNumberOfLines(5);
    this.filteredScancodeComponentInfoProvider32.setMinLicenseScore(90.0);

    this.multiversionFilteredScancodeComponentInfoProvider = new MultiversionFilteredScancodeComponentInfoProvider(
        new FilteredScancodeVersionComponentInfoProvider[] { this.filteredScancodeComponentInfoProvider32,
        this.filteredScancodeComponentInfoProvider31 }, this.fileScancodeRawComponentInfoProvider);

    this.componentInfoCuratorImpl = new ComponentInfoCuratorImpl(this.singleFileCurationProvider,
        this.fileScancodeRawComponentInfoProvider);

    this.scancodeComponentInfoAdapter = new ScancodeComponentInfoAdapter(
        this.multiversionFilteredScancodeComponentInfoProvider, this.componentInfoCuratorImpl);
    this.scancodeComponentInfoAdapter.setFeatureFlag(true);

  }

  /**
   * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when such package is not
   * known.
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   */
  @Test
  public void testGetComponentInfoNaPackage() throws ComponentInfoAdapterException, CurationInvalidException {

    // given

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter.getComponentInfo(this.unknownTestPackageURL,
        new SelectorCurationDataHandle("someCurationSelector"));

    // then
    assertNull(componentInfo.getComponentInfoData());
  }

  /**
   * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when no curations are
   * available.
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   */
  @Test
  public void testGetComponentInfoWithoutCurations() throws ComponentInfoAdapterException, CurationInvalidException {

    // given
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/nonexisting.yaml");

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter.getComponentInfo(this.testPackageURL,
        new SelectorCurationDataHandle("someCurationSelector"));

    // then
    assertNotNull(componentInfo.getComponentInfoData());
    assertEquals(this.testPackageURL, componentInfo.getPackageUrl());
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals("pkgcontent:/NOTICE.txt", componentInfo.getComponentInfoData().getNoticeFileUrl());
    assertEquals(1, componentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("Copyright 2023 devonfw", componentInfo.getComponentInfoData().getCopyrights().toArray()[0]);
    assertEquals(2, componentInfo.getComponentInfoData().getLicenses().size());

    boolean apacheFound = false;
    boolean unknownFound = false;
    for (LicenseInfo li : componentInfo.getComponentInfoData().getLicenses()) {
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
        componentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0.jar",
        componentInfo.getComponentInfoData().getPackageDownloadUrl());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0-sources.jar",
        componentInfo.getComponentInfoData().getSourceDownloadUrl());

  }

  /**
   * Test if the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} propagates the parameter
   * curationDataSelector to downstream beans.
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   */
  @Test
  public void testGetComponentCheckCurationDataSelector()
      throws ComponentInfoAdapterException, CurationInvalidException {

    // given
    CurationProvider curationProvider = Mockito.mock(CurationProvider.class);
    when(curationProvider.findCurations(Mockito.any(PackageURL.class), Mockito.any(SelectorCurationDataHandle.class)))
        .thenReturn(null);
    this.componentInfoCuratorImpl = new ComponentInfoCuratorImpl(curationProvider,
        this.fileScancodeRawComponentInfoProvider);

    this.scancodeComponentInfoAdapter = new ScancodeComponentInfoAdapter(
        this.multiversionFilteredScancodeComponentInfoProvider, this.componentInfoCuratorImpl);
    this.scancodeComponentInfoAdapter.setFeatureFlag(true);

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter.getComponentInfo(this.testPackageURL,
        new SelectorCurationDataHandle("someCurationSelector"));

    // then
    assertNotNull(componentInfo.getComponentInfoData());

    ArgumentCaptor<PackageURL> captor1 = ArgumentCaptor.forClass(PackageURL.class);
    ArgumentCaptor<SelectorCurationDataHandle> captor2 = ArgumentCaptor.forClass(SelectorCurationDataHandle.class);
    Mockito.verify(curationProvider, times(1)).findCurations(captor1.capture(), captor2.capture());
    assertEquals("someCurationSelector", captor2.getValue().getCurationDataSelector());

  }

  /**
   * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when curations are existing.
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   */
  @Test
  public void testGetComponentInfoWithCurations() throws ComponentInfoAdapterException, CurationInvalidException {

    // when
    ComponentInfo componentInfo = this.scancodeComponentInfoAdapter.getComponentInfo(this.testPackageURL,
        new SelectorCurationDataHandle("someCurationSelector"));

    // then
    assertNotNull(componentInfo.getComponentInfoData());
    assertEquals(this.testPackageURL, componentInfo.getPackageUrl());
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals("pkgcontent:/NOTICE.txt", componentInfo.getComponentInfoData().getNoticeFileUrl());
    assertEquals(1, componentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("Copyright (c) 2023 somebody", componentInfo.getComponentInfoData().getCopyrights().toArray()[0]);
    assertEquals(1, componentInfo.getComponentInfoData().getLicenses().size());

    boolean mitFound = false;
    for (LicenseInfo li : componentInfo.getComponentInfoData().getLicenses()) {
      if (li.getSpdxid().equals("MIT")) {
        Assertions.assertEquals("https://some/license/url", li.getLicenseUrl());
        mitFound = true;
      }
    }
    assertTrue(mitFound);

    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        componentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0.jar",
        componentInfo.getComponentInfoData().getPackageDownloadUrl());
    assertEquals("https://somehost/test-project-for-deep-license-scan-0.1.0-sources.jar",
        componentInfo.getComponentInfoData().getSourceDownloadUrl());
    assertEquals("http://some/url", componentInfo.getComponentInfoData().getSourceRepoUrl());
  }

}
