package com.devonfw.tools.solicitor.componentinfo.scancode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.devonfw.tools.solicitor.componentinfo.curation.FilteredComponentInfoProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.packageurl.PackageURL;

/**
 * A provider that filters and retrieves component information based on Scancode results for multiple versions. This
 * component utilizes different {@link FilteredScancodeVersionComponentInfoProvider} implementations depending on the
 * version of the Scancode tool used.
 */
@Component
public class MultiversionFilteredScancodeComponentInfoProvider implements FilteredComponentInfoProvider {

  private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private FilteredScancodeVersionComponentInfoProvider[] filteredScancodeVersionComponentInfoProviders;

  private ScancodeRawComponentInfoProvider rawComponentInfoProvider;

  /**
   * Constructs a {@link MultiversionFilteredScancodeComponentInfoProvider} with the given providers.
   *
   * @param filteredScancodeVersionComponentInfoProviders the array of
   *        {@link FilteredScancodeVersionComponentInfoProvider} implementations to use based on Scancode tool versions.
   * @param rawComponentInfoProvider the {@link ScancodeRawComponentInfoProvider} for reading raw Scancode data.
   */
  @Autowired
  public MultiversionFilteredScancodeComponentInfoProvider(
      FilteredScancodeVersionComponentInfoProvider[] filteredScancodeVersionComponentInfoProviders,
      ScancodeRawComponentInfoProvider rawComponentInfoProvider) {

    this.filteredScancodeVersionComponentInfoProviders = filteredScancodeVersionComponentInfoProviders;
    this.rawComponentInfoProvider = rawComponentInfoProvider;
  }

  @Override
  public ComponentInfo getComponentInfo(PackageURL packageUrl, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, CurationInvalidException {

    ScancodeRawComponentInfo rawScancodeData;
    JsonNode scancodeJson;

    try {
      rawScancodeData = this.rawComponentInfoProvider.readScancodeData(packageUrl);
    } catch (ScancodeProcessingFailedException e) {
      return new DefaultComponentInfoImpl(packageUrl, DataStatusValue.PROCESSING_FAILED);
    }
    if (rawScancodeData == null) {
      return new DefaultComponentInfoImpl(packageUrl, DataStatusValue.NOT_AVAILABLE);
    }

    scancodeJson = parseScancodeJson(rawScancodeData);
    String toolVersion = extractToolVersion(scancodeJson);

    for (FilteredScancodeVersionComponentInfoProvider candidate : this.filteredScancodeVersionComponentInfoProviders) {
      if (candidate.accept(toolVersion)) {
        return candidate.getComponentInfo(packageUrl, curationDataHandle, rawScancodeData, scancodeJson);
      }
    }

    throw new ComponentInfoAdapterException(
        "No suitable FilteredScancodeVersionComponentInfoProvider for given scancode JSON");
  }

  /**
   * Parses the Scancode JSON data from the raw component information.
   *
   * @param rawScancodeData the raw Scancode data containing JSON results.
   * @return the parsed {@link JsonNode} from the Scancode JSON data.
   * @throws ComponentInfoAdapterException if an error occurs while parsing the JSON data.
   */
  private JsonNode parseScancodeJson(ScancodeRawComponentInfo rawScancodeData) throws ComponentInfoAdapterException {

    try {
      return mapper.readTree(rawScancodeData.rawScancodeResult);
    } catch (JsonProcessingException e) {
      throw new ComponentInfoAdapterException("Could not parse Scancode JSON", e);
    }
  }

  /**
   * Extracts the tool version from the Scancode JSON data.
   *
   * @param scancodeJson the parsed Scancode JSON data.
   * @return the tool version as a {@link String}.
   * @throws ComponentInfoAdapterException if an error occurs while extracting the tool version.
   */
  private String extractToolVersion(JsonNode scancodeJson) throws ComponentInfoAdapterException {

    JsonNode headers = scancodeJson.get("headers");
    if (headers == null || !headers.isArray() || headers.size() == 0) {
      throw new ComponentInfoAdapterException("Headers not found in Scancode JSON");
    }
    JsonNode firstHeader = headers.get(0);
    if (firstHeader == null) {
      throw new ComponentInfoAdapterException("First header not found in Scancode JSON");
    }
    JsonNode toolVersionNode = firstHeader.get("tool_version");
    if (toolVersionNode == null) {
      throw new ComponentInfoAdapterException("Tool version not found in Scancode JSON");
    }
    return toolVersionNode.asText();

  }
}