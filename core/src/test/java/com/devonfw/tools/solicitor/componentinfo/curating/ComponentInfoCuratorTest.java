package com.devonfw.tools.solicitor.componentinfo.curating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.componentinfo.ComponentContentProvider;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoAdapterNonExistingCurationDataSelectorException;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCurator;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCuratorImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Unit tests for the {@link ComponentInfoCurator} class.
 */
public class ComponentInfoCuratorTest {

  private ComponentInfoCurator componentInfoCurator;

  @BeforeEach
  public void setup()
      throws ComponentInfoAdapterNonExistingCurationDataSelectorException, ComponentInfoAdapterException {

    // Mocking required dependencies
    CurationProvider curationProvider = mock(CurationProvider.class);
    when(curationProvider.findCurations("pkg/maven/somenamespace/somecomponent/2.3.4", "none")).thenReturn(null);

    // Create an instance of ComponentInfoCuratorImpl
    this.componentInfoCurator = new ComponentInfoCuratorImpl(curationProvider, null);
  }

  /**
   * Test the {@link ComponentInfoCurator#curate(ComponentInfo, String)} method with "none" selector.
   *
   * <p>
   * Given a sample {@link ComponentInfo}, when the {@code curate} method is called with "none" selector, then verify
   * that the returned {@link ComponentInfo} is the same as the input, and ensure that the package URL is not modified.
   * </p>
   *
   * @throws ComponentInfoAdapterException If an error occurs during the test.
   */
  @Test
  public void testCurateWithNoneSelector() throws ComponentInfoAdapterException {

    // Create a sample ComponentInfo
    ComponentInfo componentInfo = mock(ComponentInfo.class);
    when(componentInfo.getPackageUrl()).thenReturn("pkg/maven/somenamespace/somecomponent/2.3.4");

    // Call the curate method with "none" selector
    ComponentInfo curatedComponentInfo = this.componentInfoCurator.curate(componentInfo, "none");

    // Verify that the returned ComponentInfo is the same as the input
    assertEquals(componentInfo, curatedComponentInfo);

    // Ensure that the curatedComponentInfo has the same package URL
    assertEquals("pkg/maven/somenamespace/somecomponent/2.3.4", curatedComponentInfo.getPackageUrl());

  }

  /**
   * Test the {@link ComponentInfoCurator#curate(ComponentInfo, String)} method with raw ScanCode results.
   *
   * <p>
   * Given a mocked {@link CurationProvider} that returns null for the specified package URL and curation data selector,
   * when the {@code curate} method is called with raw ScanCode results, then verify that the result is not null, has
   * the expected package URL, and performs cleanup of the created YAML file.
   * </p>
   *
   * @throws ComponentInfoAdapterException If an error occurs during the test.
   * @throws IOException If an error occurs during the test.
   */
  @Test
  public void testCurateWithRawScanCodeResults() throws ComponentInfoAdapterException, IOException {

    // Mock dependencies
    CurationProvider curationProvider = mock(CurationProvider.class);
    ComponentContentProvider componentContentProvider = mock(ComponentContentProvider.class);

    // Create an instance of ComponentInfoCuratorImpl
    ComponentInfoCuratorImpl curator = new ComponentInfoCuratorImpl(curationProvider, componentContentProvider);

    // Mock a ComponentInfo
    ComponentInfo componentInfo = mock(ComponentInfo.class);
    when(componentInfo.getPackageUrl()).thenReturn("example-package-url");

    // Mock the behavior of CurationProvider to return null for foundCuration
    when(curationProvider.findCurations("example-package-url", "none")).thenReturn(null);

    // Create a sample YAML file for raw ScanCode results
    String rawScanCodeResultsYaml = "packageUrl: example-package-url\n"
        + "licenses:\n  - license: Apache-2.0\n    url: https://example.com/apache-2.0\n";

    File rawScanCodeResultsFile = new File("src/test/resources/rawScanCodeResults.yaml");
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.writeValue(rawScanCodeResultsFile, mapper.readValue(rawScanCodeResultsYaml, Object.class));

    // Invoke the curate method with curationDataSelector set to "none"
    ComponentInfo result = curator.curate(componentInfo, "none");

    // Verify that the result is not null
    assertNotNull(result);

    // Verify that the result has the expected package URL
    assertEquals("example-package-url", result.getPackageUrl());

    // Clean up the created YAML file
    rawScanCodeResultsFile.delete();
  }
}