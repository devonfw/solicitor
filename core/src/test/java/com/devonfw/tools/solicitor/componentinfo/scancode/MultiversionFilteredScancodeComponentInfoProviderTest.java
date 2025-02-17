package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.packageurl.PackageURL;

/**
 * Unit test for {@link MultiversionFilteredScancodeComponentInfoProvider}.
 *
 * This test class verifies the behavior of the {@link MultiversionFilteredScancodeComponentInfoProvider} class,
 * ensuring it correctly delegates component information retrieval to the appropriate version-specific provider based on
 * the version of the Scancode data provided.
 */
public class MultiversionFilteredScancodeComponentInfoProviderTest {

  @Mock
  private FilteredScancodeV31ComponentInfoProvider filteredScancodeV31Provider;

  @Mock
  private FilteredScancodeV32ComponentInfoProvider filteredScancodeV32Provider;

  @Mock
  private ScancodeRawComponentInfoProvider rawComponentInfoProvider;

  private MultiversionFilteredScancodeComponentInfoProvider provider;

  private ObjectMapper mapper = new ObjectMapper();

  /**
   * Sets up the test environment by initializing mocks and the
   * {@link MultiversionFilteredScancodeComponentInfoProvider} instance.
   */
  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
    when(this.filteredScancodeV31Provider.accept("31.2.6")).thenReturn(true);
    when(this.filteredScancodeV31Provider.accept("32.1.0")).thenReturn(false);
    when(this.filteredScancodeV31Provider.accept("99.1.0")).thenReturn(false);
    when(this.filteredScancodeV32Provider.accept("31.2.6")).thenReturn(false);
    when(this.filteredScancodeV32Provider.accept("32.1.0")).thenReturn(true);
    when(this.filteredScancodeV32Provider.accept("99.1.0")).thenReturn(false);
    this.provider = new MultiversionFilteredScancodeComponentInfoProvider(
        new FilteredScancodeVersionComponentInfoProvider[] { this.filteredScancodeV31Provider,
        this.filteredScancodeV32Provider }, this.rawComponentInfoProvider);
  }

  /**
   * Loads JSON data from a file located in the test resources directory.
   *
   * @param fileName the name of the file to load
   * @return the JSON data as a string
   * @throws IOException if an error occurs while reading the file
   */
  private String loadJsonData(String fileName) throws IOException {

    return new String(Files.readAllBytes(Paths.get("src/test/resources/scancode/" + fileName)));
  }

  /**
   * Tests the {@link MultiversionFilteredScancodeComponentInfoProvider#getComponentInfo} method when version 31 is
   * used.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void testGetComponentInfoVersion31() throws Exception {

    PackageURL packageUrl = PackageURLHelper.fromString("pkg:maven/com.mycompany/mycomponent@1.0.0");
    CurationDataHandle curationDataHandle = mock(CurationDataHandle.class);

    String jsonData = loadJsonData("scancode_v31.json");
    ScancodeRawComponentInfo rawScancodeData = new ScancodeRawComponentInfo();
    rawScancodeData.rawScancodeResult = jsonData;

    JsonNode scancodeJson = this.mapper.readTree(rawScancodeData.rawScancodeResult);

    when(this.rawComponentInfoProvider.readScancodeData(packageUrl)).thenReturn(rawScancodeData);
    when(this.filteredScancodeV31Provider.getComponentInfo(eq(packageUrl), eq(curationDataHandle), any(), any()))
        .thenReturn(mock(ComponentInfo.class));

    ComponentInfo result = this.provider.getComponentInfo(packageUrl, curationDataHandle);

    assertNotNull(result);
    verify(this.filteredScancodeV31Provider).getComponentInfo(eq(packageUrl), eq(curationDataHandle), any(), any());
  }

  /**
   * Tests the {@link MultiversionFilteredScancodeComponentInfoProvider#getComponentInfo} method when version 32 is
   * used.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void testGetComponentInfoVersion32() throws Exception {

    PackageURL packageUrl = PackageURLHelper.fromString("pkg:maven/com.mycompany/mycomponent@1.0.0");
    CurationDataHandle curationDataHandle = mock(CurationDataHandle.class);

    String jsonData = loadJsonData("scancode_v32.json");
    ScancodeRawComponentInfo rawScancodeData = new ScancodeRawComponentInfo();
    rawScancodeData.rawScancodeResult = jsonData;

    // JsonNode scancodeJson = new ObjectMapper().readTree(rawScancodeData.rawScancodeResult);

    when(this.rawComponentInfoProvider.readScancodeData(packageUrl)).thenReturn(rawScancodeData);
    when(this.filteredScancodeV32Provider.getComponentInfo(eq(packageUrl), eq(curationDataHandle), any(), any()))
        .thenReturn(mock(ComponentInfo.class));

    ComponentInfo result = this.provider.getComponentInfo(packageUrl, curationDataHandle);

    assertNotNull(result);
    verify(this.filteredScancodeV32Provider).getComponentInfo(eq(packageUrl), eq(curationDataHandle), any(), any());
  }

  /**
   * Tests the {@link MultiversionFilteredScancodeComponentInfoProvider#getComponentInfo} method when an unsupported
   * version is used.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void testGetComponentInfoVersionNotSupported() throws Exception {

    PackageURL packageUrl = PackageURLHelper.fromString("pkg:maven/com.mycompany/mycomponent@1.0.0");
    CurationDataHandle curationDataHandle = mock(CurationDataHandle.class);

    String jsonData = loadJsonData("scancode_v99.json");
    ScancodeRawComponentInfo rawScancodeData = new ScancodeRawComponentInfo();
    rawScancodeData.rawScancodeResult = jsonData;

    when(this.rawComponentInfoProvider.readScancodeData(packageUrl)).thenReturn(rawScancodeData);

    assertThrows(ComponentInfoAdapterException.class, () -> {
      this.provider.getComponentInfo(packageUrl, curationDataHandle);
    });
  }

  /**
   * This test ensures that the provider returns a {@link ComponentInfo} with a {@link DataStatusValue#NOT_AVAILABLE}
   * status if the raw Scancode data is null.
   *
   * @throws Exception if an error occurs during the test
   */
  @Test
  void testGetComponentInfoScancodeDataNull() throws Exception {

    // Arrange
    PackageURL packageUrl = PackageURLHelper.fromString("pkg:maven/com.mycompany/mycomponent@1.0.0");
    CurationDataHandle curationDataHandle = mock(CurationDataHandle.class);

    when(this.rawComponentInfoProvider.readScancodeData(packageUrl)).thenReturn(null);

    // Act
    ComponentInfo result = this.provider.getComponentInfo(packageUrl, curationDataHandle);

    // Assert
    assertNotNull(result);
    assertEquals(DataStatusValue.NOT_AVAILABLE, result.getDataStatus());
  }

}
