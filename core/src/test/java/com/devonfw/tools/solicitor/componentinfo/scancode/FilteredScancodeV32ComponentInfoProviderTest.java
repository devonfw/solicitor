package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.SelectorCurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class contains JUnit test methods for the {@link FilteredScancodeV31ComponentInfoProvider} class.
 */
public class FilteredScancodeV32ComponentInfoProviderTest {

  // the object under test
  FilteredScancodeV32ComponentInfoProvider filteredScancodeV32ComponentInfoProvider;

  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  SingleFileCurationProvider singleFileCurationProvider;

  @BeforeEach
  public void setup() {

    AllKindsPackageURLHandler packageURLHandler = Mockito.mock(AllKindsPackageURLHandler.class);

    Mockito.when(packageURLHandler.pathFor("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0"))
        .thenReturn("pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0");

    this.fileScancodeRawComponentInfoProvider = new FileScancodeRawComponentInfoProvider(packageURLHandler);
    this.fileScancodeRawComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

    this.singleFileCurationProvider = new SingleFileCurationProvider(packageURLHandler);
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    this.filteredScancodeV32ComponentInfoProvider = new FilteredScancodeV32ComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, this.singleFileCurationProvider);

  }

  /**
   * Test the
   * {@link FilteredScancodeV31ComponentInfoProvider#getComponentInfo(String,String,ScancodeRawComponentInfo,JsonNode)}
   * method when no curations file exists
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException if the curation data is not valid
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoWithoutCurations() throws ComponentInfoAdapterException, CurationInvalidException,
      ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/nonexisting.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeV32ComponentInfoProvider.getComponentInfo(
        "pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0",
        new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

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
   * Test the
   * {@link FilteredScancodeV31ComponentInfoProvider#getComponentInfo(String,String,ScancodeRawComponentInfo,JsonNode)}
   * method when the /src directory is excluded
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException if the curation data is not valid
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoWithCurationsAndExclusions() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/curations_with_exclusions.yaml");

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    // when
    ComponentInfo scancodeComponentInfo = this.filteredScancodeV32ComponentInfoProvider.getComponentInfo(
        "pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0",
        new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

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
   * Test the
   * {@link FilteredScancodeV31ComponentInfoProvider#getComponentInfo(String,String,ScancodeRawComponentInfo,JsonNode)}
   * method when curations exist but no paths are excluded
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException if the curation data is not valid
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoWithCurationsAndWithoutExclusions() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    // when
    ComponentInfo scancodeComponentInfo = this.filteredScancodeV32ComponentInfoProvider.getComponentInfo(
        "pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0",
        new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

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
