package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeLicenseInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for {@link ScancodeJsonParser}.
 */
class ScancodeJsonParserTest {

  private ScancodeRawComponentInfoProvider mockProvider;

  private ScancodeRawComponentInfo rawScancodeData;

  private ScancodeComponentInfo componentScancodeInfos;

  private ScancodeComponentInfo.ScancodeComponentInfoData scancodeComponentInfoData;

  private ComponentInfoCuration componentInfoCuration;

  /**
   * Sets up the test environment before each test.
   *
   * @throws IOException if an I/O error occurs.
   */
  @BeforeEach
  void setUp() throws IOException {

    this.mockProvider = Mockito.mock(ScancodeRawComponentInfoProvider.class);
    this.componentScancodeInfos = new ScancodeComponentInfo(20.0, 10);
    this.scancodeComponentInfoData = this.componentScancodeInfos.getComponentInfoData();
    this.componentInfoCuration = new ComponentInfoCuration();
  }

  /**
   * Tests the parsing of Scancode version 3.1 JSON data.
   *
   * @throws IOException if an I/O error occurs.
   * @throws ComponentInfoAdapterException if a component info adapter error occurs.
   */
  @Test
  void testParseScancodeV31() throws IOException, ComponentInfoAdapterException {

    // Load mock data from JSON file (version 31)
    String jsonData = loadJsonData("scancode/scancode_v31.json");
    this.rawScancodeData = new ScancodeRawComponentInfo();
    this.rawScancodeData.rawScancodeResult = jsonData;

    JsonNode scancodeJson = new ObjectMapper().readTree(this.rawScancodeData.rawScancodeResult);

    assertNotNull(scancodeJson);

    ScancodeJsonParserV31 parser = new ScancodeJsonParserV31(this.mockProvider, "mockPackageUrl",
        this.componentScancodeInfos, this.scancodeComponentInfoData, this.componentInfoCuration);

    // Perform parsing
    ScancodeComponentInfo result = parser.parse(scancodeJson, 20.0);

    // Validate the results
    assertNotNull(result);

    // Check if the combined license expression is correctly parsed
    assertEquals(1, result.getComponentInfoData().getLicenses().size());
    ScancodeLicenseInfo licenseInfo = result.getComponentInfoData().getLicenses().iterator().next();
    // assertEquals("MIT", licenseInfo.getSpdxid());
    assertEquals("MIT", licenseInfo.getSpdxid());
    assertEquals("https://scancode-licensedb.aboutcode.org/mit.LICENSE", licenseInfo.getLicenseUrl());
    assertEquals(100.0, licenseInfo.getLicenseScore());
    // Check copyright information
    assertEquals(1, result.getComponentInfoData().getCopyrights().size());
    // assertTrue(result.getComponentInfoData().getCopyrights().contains("Copyright (c) 2024 MIT License"));

  }

  /**
   * Tests the parsing of Scancode version 3.2 JSON data.
   *
   * @throws IOException if an I/O error occurs.
   * @throws ComponentInfoAdapterException if a component info adapter error occurs.
   */
  @Test
  void testParseScancodeV32() throws IOException, ComponentInfoAdapterException {

    // Load mock data from JSON file (version 32)
    String jsonData = loadJsonData("scancode/scancode_v32.json");
    this.rawScancodeData = new ScancodeRawComponentInfo();
    this.rawScancodeData.rawScancodeResult = jsonData;

    JsonNode scancodeJson = new ObjectMapper().readTree(this.rawScancodeData.rawScancodeResult);

    assertNotNull(scancodeJson);

    ScancodeJsonParserV32 parser = new ScancodeJsonParserV32(this.mockProvider, "mockPackageUrl",
        this.componentScancodeInfos, this.scancodeComponentInfoData, this.componentInfoCuration);

    // Perform parsing
    ScancodeComponentInfo result = parser.parse(scancodeJson, 20.0);
    // Validate the results
    assertNotNull(result);

    // Check if the combined license expression is correctly parsed
    assertEquals(1, result.getComponentInfoData().getLicenses().size());
    ScancodeLicenseInfo licenseInfo = result.getComponentInfoData().getLicenses().iterator().next();
    // assertEquals("MIT", licenseInfo.getSpdxid());
    assertEquals("MIT", licenseInfo.getSpdxid());
    assertEquals("https://scancode-licensedb.aboutcode.org/mit.LICENSE", licenseInfo.getLicenseUrl());
    assertEquals(100.0, licenseInfo.getLicenseScore());

  }

  /**
   * Loads JSON data from a file.
   *
   * @param fileName the name of the file.
   * @return the JSON data as a string.
   * @throws IOException if an I/O error occurs.
   */
  private String loadJsonData(String fileName) throws IOException {

    return new String(Files.readAllBytes(Paths.get("src/test/resources/" + fileName)));
  }

}
