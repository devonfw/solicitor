package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider;

/**
 * This class contains JUnit test methods for the {@link FilteredScancodeComponentInfoProvider} class.
 */
public class FilteredScancodeComponentInfoProviderTests {

  // the object under test
  FilteredScancodeComponentInfoProvider filteredScancodeComponentInfoProvider;

  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

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

    this.singleFileCurationProvider = new SingleFileCurationProvider(packageURLHandler);
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    this.filteredScancodeComponentInfoProvider = new FilteredScancodeComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, packageURLHandler, this.singleFileCurationProvider);

  }

  /**
   * Test the {@link FilteredScancodeComponentInfoProvider#getComponentInfo(String,String)} method when no curations
   * file exists
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   */
  @Test
  public void testGetComponentInfoWithoutCurations() throws ComponentInfoAdapterException {

    // given
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/nonexisting.yaml");

    // when
    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider
        .getComponentInfo("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", "none");

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0",
        scancodeComponentInfo.getPackageUrl());
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        scancodeComponentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("Copyright 2023 devonfw", scancodeComponentInfo.getComponentInfoData().getCopyrights().toArray()[0]);
  }

  /**
   * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when the /src directory is
   * excluded
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   */
  @Test
  public void testGetComponentInfoWithCurationsAndExclusions() throws ComponentInfoAdapterException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/curations_with_exclusions.yaml");
    // when
    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        "pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", "someCurationSelector");

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0",
        scancodeComponentInfo.getPackageUrl());
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        scancodeComponentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals(0, scancodeComponentInfo.getComponentInfoData().getCopyrights().size()); // since the copyright is
                                                                                          // found under
    // /src/../SampleClass.java1, it will be excluded
  }

  /**
   * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when curations exist but no
   * paths are excluded
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   */
  @Test
  public void testGetComponentInfoWithCurationsAndWithoutExclusions() throws ComponentInfoAdapterException {

    // given
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    // when
    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        "pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", "someCurationSelector");

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0",
        scancodeComponentInfo.getPackageUrl());
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
        scancodeComponentInfo.getComponentInfoData().getNoticeFileContent());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("Copyright 2023 devonfw", scancodeComponentInfo.getComponentInfoData().getCopyrights().toArray()[0]); // The
                                                                                                                       // copyright
    // curation does not
    // apply on the
    // scancodeComponentInfo
    // object.
  }
}
