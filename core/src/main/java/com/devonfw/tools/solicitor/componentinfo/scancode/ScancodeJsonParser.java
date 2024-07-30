package com.devonfw.tools.solicitor.componentinfo.scancode;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for Scancode JSON parsers.
 * <p>
 * This interface defines methods for parsing and processing Scancode JSON data, setting licenses, reformatting license
 * data, and setting license status based on different Scancode versions.
 * </p>
 */
public interface ScancodeJsonParser {

  /**
   * Parses Scancode JSON data to extract component information.
   *
   * @param scancodeJson The JSON data from Scancode to parse. This should contain license and component information.
   * @param licenseToTextRatioToTakeCompleteFile The ratio of license text to determine if the complete file should be
   *        considered.
   * @return A {@link ScancodeComponentInfo} object containing the parsed component information.
   * @throws ComponentInfoAdapterException If an error occurs during parsing, such as an invalid JSON format or missing
   *         data.
   */
  ScancodeComponentInfo parse(JsonNode scancodeJson, double licenseToTextRatioToTakeCompleteFile)
      throws ComponentInfoAdapterException;

  /**
   * Sets license information from the provided JSON node.
   *
   * @param licenses The JSON node containing license information. This includes details about matched licenses.
   * @param path The path of the file from which the licenses were extracted, used for context in processing.
   */
  void setLicenses(JsonNode licenses, String path);

  /**
   * Reformats and updates license data based on the provided effective license information.
   *
   * @param license JSON node containing license information. This includes details about the license matches.
   * @param effective The effective license data that should be used for curation or modification.
   * @param path The path of the file in the Scancode data, used to identify the source of the license information.
   * @param takeCompleteFile Flag indicating whether to consider the entire file or just portions of it for the license
   *        data.
   */
  void reformatData(JsonNode license, LicenseCuration.NewLicenseData effective, String path, boolean takeCompleteFile);

  /**
   * Sets the status of the license based on the provided JSON data.
   *
   * @param license JSON node containing license information, including details about the license matches.
   * @param path The path of the file in the Scancode data, used for contextual reference.
   * @return The {@link LicenseCuration.NewLicenseData} object representing the effective license data after applying
   *         curation.
   */
  LicenseCuration.NewLicenseData setStatus(JsonNode license, String path);
}
