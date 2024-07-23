package com.devonfw.tools.solicitor.componentinfo.scancode;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration.NewLicenseData;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeComponentInfoData;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation of {@link ScancodeJsonParser} for Scancode version 31. This parser processes Scancode JSON results
 * specific to version 31 to extract and curate component information.
 */
public class ScancodeJsonParserV31 extends ScancodeJsonParserEngine implements ScancodeJsonParser {

  /**
   * Constructor for ScancodeJsonParserV31.
   *
   * @param fileScancodeRawComponentInfoProvider the provider for raw component information.
   * @param packageUrl the package URL.
   * @param componentScancodeInfos the Scancode component information.
   * @param scancodeComponentInfoData the Scancode component information data.
   * @param componentInfoCuration the component information curation.
   */
  public ScancodeJsonParserV31(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider, String packageUrl,
      ScancodeComponentInfo componentScancodeInfos, ScancodeComponentInfoData scancodeComponentInfoData,
      ComponentInfoCuration componentInfoCuration) {

    super(fileScancodeRawComponentInfoProvider, packageUrl, componentScancodeInfos, scancodeComponentInfoData,
        componentInfoCuration);
  }

  /**
   * Parses the given Scancode JSON data and extracts component information.
   *
   * @param scancodeJson JSON node containing the Scancode scan results.
   * @param licenseToTextRatioToTakeCompleteFile Ratio used to determine if the complete file should be considered.
   * @return {@link ScancodeComponentInfo} object containing the extracted and curated component information.
   * @throws ComponentInfoAdapterException If an error occurs during parsing.
   */
  @Override
  public ScancodeComponentInfo parse(JsonNode scancodeJson, double licenseToTextRatioToTakeCompleteFile)
      throws ComponentInfoAdapterException {

    return parse(scancodeJson, licenseToTextRatioToTakeCompleteFile, "licenses", this);
  }

  /**
   * Reformats and updates license information with effective data for Scancode version 31.
   *
   * @param license JSON node containing license information.
   * @param effective The effective license data for curation.
   * @param path Path of the file in the Scancode data.
   * @param takeCompleteFile Flag indicating if the complete file should be considered.
   */
  @Override
  public void reformatData(JsonNode license, LicenseCuration.NewLicenseData effective, String path,
      boolean takeCompleteFile) {

    if (effective == null || this.spdxIdMap == null) {
      // Log or handle the error appropriately
      System.err.println("Effective license or spdxIdMap is null");
      return;
    }

    String spdxId = license.get("spdx_license_key").asText();
    if (spdxId == null)
      return;

    reformatData(license, effective, path, takeCompleteFile, spdxId, license.get("scancode_text_url").asText(),
        license.get("score").asDouble(), license.get("start_line").asInt(), license.get("end_line").asInt());

  }

  /**
   * Sets licenses for the provided JSON node and updates the component information accordingly.
   *
   * @param licenses JSON node containing a list of licenses.
   * @param path Path of the file in the Scancode data.
   */
  @Override
  public void setLicenses(JsonNode licenses, String path) {

    for (JsonNode li : licenses) {
      String ruleIdentifier = li.get("matched_rule").get("identifier").asText();
      String matchedText = li.get("matched_text").asText();
      String spdxId = li.get("spdx_license_key").asText();

      LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText,
          spdxId, this.licenseCurations);
      if (effective == null) {
        // license finding to be REMOVED via finding
        continue;
      }
      String licenseName = effective.license != null ? effective.license : spdxId;

      if ("Classpath-exception-2.0".equals(licenseName)) {
        this.classPathExceptionExists = true;
      }
      if (!this.spdxIdMap.containsKey(licenseName)) {
        this.spdxIdMap.put(licenseName, licenseName);
        if (licenseName.startsWith("GPL")) {
          this.numberOfGplLicenses++;
        }
      }
    }

  }

  /**
   * Determines the license status for the given JSON node and updates component information accordingly.
   *
   * @param license JSON node containing license information.
   * @param path Path of the file in the Scancode data.
   * @return The effective license data after applying curation, or null if no effective data is found.
   */
  @Override
  public NewLicenseData setStatus(JsonNode license, String path) {

    String ruleIdentifier = license.get("matched_rule").get("identifier").asText();
    String matchedText = license.get("matched_text").asText();
    String spdxId = license.get("spdx_license_key").asText();

    LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText,
        spdxId, this.licenseCurations);
    if (effective == null) {
      // license finding to be REMOVED via finding
      // this is a curation operation, so set the status
      this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
      return null;
    }
    if (effective.license != null || effective.url != null) {
      // license or url are altered due to curation, so set the status
      this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
    }
    String licenseName = effective.license != null ? effective.license : spdxId;
    return effective;
  }

}
