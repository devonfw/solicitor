package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.LicenseInfo;
import com.devonfw.tools.solicitor.componentinfo.SelectorCurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.packageurl.PackageURL;

/**
 * This class contains JUnit test methods for the testing the raw curations of
 * {@link FilteredScancodeV32ComponentInfoProvider} class.
 */

public class RawCurationTestV32 {

  PackageURL testPackageURL;

  // the object under test
  FilteredScancodeV32ComponentInfoProvider filteredScancodeComponentInfoProvider;

  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  SingleFileCurationProvider singleFileCurationProvider;

  @BeforeEach
  public void setup() throws SolicitorMalformedPackageURLException {

    this.testPackageURL = PackageURLHelper
        .fromString("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0");

    AllKindsPackageURLHandler packageURLHandler = Mockito.mock(AllKindsPackageURLHandler.class);
    Mockito.when(packageURLHandler.pathFor(this.testPackageURL))
        .thenReturn("pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0");

    this.fileScancodeRawComponentInfoProvider = new FileScancodeRawComponentInfoProvider(packageURLHandler);
    this.fileScancodeRawComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

    this.singleFileCurationProvider = new SingleFileCurationProvider(packageURLHandler);
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");

    this.filteredScancodeComponentInfoProvider = new FilteredScancodeV32ComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, this.singleFileCurationProvider);

  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_AllConditionsSetAndMet() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_remove_curation_1.yaml");
    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
    assertEquals("Apache-2.0",
        scancodeComponentInfo.getComponentInfoData().getLicenses().iterator().next().getSpdxid());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_AllConditionsSetAndPathNotMet() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_remove_curation_2.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_AllConditionsSetAndRuleIdentifierNotMet()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_remove_curation_3.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_AllConditionsSetAndOldLicenseNotMet()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_remove_curation_4.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_AllConditionsSetAndMatchedTextNotMet()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_remove_curation_5.yaml");

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_OnlyPathConditionSetAndMetForAllFiles()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_remove_curation_7.yaml");

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(0, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */

  @Test
  public void testGetComponentInfoRawLicenseReplace_AllConditionsSetAndMet() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_replace_curation_1.yaml");

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
    boolean found = false;
    for (LicenseInfo license : scancodeComponentInfo.getComponentInfoData().getLicenses()) {
      if (license.getSpdxid().equals("NewLicense")) {
        found = true;
        assertEquals("pkgcontent:/src/main/java/com/devonfw/tools/test/SampleClass2.java#L3", license.getLicenseUrl());
        assertTrue(license.getGivenLicenseText()
            .startsWith(" * This file is part of the test data for deep license scan support in Solicitor.."));
      }
    }
    assertTrue(found);
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseReplace_AllConditionsSetAndMetOnlyLicenseReplaced()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_replace_curation_2.yaml");

    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
    boolean found = false;
    for (LicenseInfo license : scancodeComponentInfo.getComponentInfoData().getLicenses()) {
      if (license.getSpdxid().equals("NewLicense")) {
        found = true;
        assertEquals("pkgcontent:/src/main/java/com/devonfw/tools/test/SampleClass2.java#L4", license.getLicenseUrl());
        assertTrue(license.getGivenLicenseText()
            .startsWith(" * It is licensed under the same license as the rest of Solicitor."));
      }
    }
    assertTrue(found);
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   * @throws ScancodeProcessingFailedException
   */
  @Test
  public void testGetComponentInfoRawLicenseReplace_AllConditionsSetAndMetOnlyUrlReplaced()
      throws ComponentInfoAdapterException, CurationInvalidException, JsonMappingException, JsonProcessingException,
      ScancodeProcessingFailedException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_replace_curation_3.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
    boolean found = false;
    for (LicenseInfo license : scancodeComponentInfo.getComponentInfoData().getLicenses()) {
      if (license.getSpdxid().equals("LicenseRef-scancode-unknown-license-reference")) {
        found = true;
        assertEquals("pkgcontent:/src/main/java/com/devonfw/tools/test/SampleClass2.java#L3", license.getLicenseUrl());
        assertTrue(license.getGivenLicenseText()
            .startsWith(" * This file is part of the test data for deep license scan support in Solicitor.."));
      }
    }
    assertTrue(found);
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseAdd_WithPath() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_add_curation_1.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(3, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
    boolean found = false;
    for (LicenseInfo license : scancodeComponentInfo.getComponentInfoData().getLicenses()) {
      if (license.getSpdxid().equals("NewLicense")) {
        found = true;
        assertEquals("pkgcontent:/src/main/java/com/devonfw/tools/test/SampleClass2.java#L3", license.getLicenseUrl());
        assertTrue(license.getGivenLicenseText()
            .startsWith(" * This file is part of the test data for deep license scan support in Solicitor.."));
      }
    }
    assertTrue(found);
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseAdd_WithoutPath() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_add_curation_2.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(3, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
    boolean found = false;
    for (LicenseInfo license : scancodeComponentInfo.getComponentInfoData().getLicenses()) {
      if (license.getSpdxid().equals("NewLicense")) {
        found = true;
        assertEquals("pkgcontent:/src/main/java/com/devonfw/tools/test/SampleClass2.java#L3", license.getLicenseUrl());
        assertTrue(license.getGivenLicenseText()
            .startsWith(" * This file is part of the test data for deep license scan support in Solicitor.."));
      }
    }
    assertTrue(found);
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseAdd_WithPathNotMatching() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/license_add_curation_3.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getLicenses().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightRemove_AllConditionsSetAndMet() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_remove_curation_1.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(0, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightRemove_AllConditionsSetAndPathNotMet()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_remove_curation_2.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("Copyright 2023 devonfw",
        scancodeComponentInfo.getComponentInfoData().getCopyrights().iterator().next());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawLicenseRemove_AllConditionsSetAndOldCopyrightNotMet()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_remove_curation_4.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("Copyright 2023 devonfw",
        scancodeComponentInfo.getComponentInfoData().getCopyrights().iterator().next());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightRemove_OnlyPathConditionSetAndMetForAllFiles()
      throws ComponentInfoAdapterException, CurationInvalidException, ScancodeProcessingFailedException,
      JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_remove_curation_7.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(0, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightReplace_AllConditionsSetAndMet() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_replace_curation_1.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);
    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertEquals("(c) 2023 devonfw", scancodeComponentInfo.getComponentInfoData().getCopyrights().iterator().next());
  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightAdd_WithPath() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_add_curation_1.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertTrue(scancodeComponentInfo.getComponentInfoData().getCopyrights().contains("(c) 2024 devonfw"));
    assertTrue(scancodeComponentInfo.getComponentInfoData().getCopyrights().contains("Copyright 2023 devonfw"));

  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightAdd_WithoutPath() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_add_curation_2.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(2, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertTrue(scancodeComponentInfo.getComponentInfoData().getCopyrights().contains("(c) 2024 devonfw"));
    assertTrue(scancodeComponentInfo.getComponentInfoData().getCopyrights().contains("Copyright 2023 devonfw"));

  }

  /**
   * Test the
   * {@link FilteredScancodeV32ComponentInfoProvider#getComponentInfo(String, com.devonfw.tools.solicitor.componentinfo.CurationDataHandle)}
   *
   *
   * @throws ComponentInfoAdapterException if something goes wrong
   * @throws CurationInvalidException
   * @throws ScancodeProcessingFailedException
   * @throws JsonProcessingException
   * @throws JsonMappingException
   */
  @Test
  public void testGetComponentInfoRawCopyrightAdd_WithPathNotMatching() throws ComponentInfoAdapterException,
      CurationInvalidException, ScancodeProcessingFailedException, JsonMappingException, JsonProcessingException {

    // given
    this.singleFileCurationProvider
        .setCurationsFileName("src/test/resources/scancodefileadapter/rawcurations/copyright_add_curation_3.yaml");

    // when
    ScancodeRawComponentInfo rawScancodeData = this.fileScancodeRawComponentInfoProvider
        .readScancodeData(this.testPackageURL);
    JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    ComponentInfo scancodeComponentInfo = this.filteredScancodeComponentInfoProvider.getComponentInfo(
        this.testPackageURL, new SelectorCurationDataHandle("someCurationSelector"), rawScancodeData, scancodeJson);

    // then
    assertNotNull(scancodeComponentInfo.getComponentInfoData());
    assertNotEquals(DataStatusValue.CURATED, scancodeComponentInfo.getDataStatus());
    assertEquals(1, scancodeComponentInfo.getComponentInfoData().getCopyrights().size());
    assertTrue(scancodeComponentInfo.getComponentInfoData().getCopyrights().contains("Copyright 2023 devonfw"));

  }

}
