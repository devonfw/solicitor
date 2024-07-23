package com.devonfw.tools.solicitor.componentinfo.scancode;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration.NewLicenseData;
import com.devonfw.tools.solicitor.componentinfo.scancode.ScancodeComponentInfo.ScancodeComponentInfoData;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Implementation of {@link ScancodeJsonParser} for Scancode version 32. This parser processes Scancode JSON results to
 * extract and curate component information according to the rules defined in the Scancode version 32 format.
 */
public class ScancodeJsonParserV32 extends ScancodeJsonParserEngine implements ScancodeJsonParser {

  /**
   * Constructor for ScancodeJsonParserV32.
   *
   * @param fileScancodeRawComponentInfoProvider the provider for raw component information.
   * @param packageUrl the package URL.
   * @param componentScancodeInfos the Scancode component information.
   * @param scancodeComponentInfoData the Scancode component information data.
   * @param componentInfoCuration the component information curation.
   */
  public ScancodeJsonParserV32(ScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider, String packageUrl,
      ScancodeComponentInfo componentScancodeInfos, ScancodeComponentInfoData scancodeComponentInfoData,
      ComponentInfoCuration componentInfoCuration) {

    super(fileScancodeRawComponentInfoProvider, packageUrl, componentScancodeInfos, scancodeComponentInfoData,
        componentInfoCuration);
  }

  /**
   * Parses the provided Scancode JSON data and extracts component information.
   *
   * @param scancodeJson JSON node containing the Scancode scan results.
   * @param licenseToTextRatioToTakeCompleteFile Ratio to determine if the complete file should be considered.
   * @return {@link ScancodeComponentInfo} containing the extracted and curated component information.
   * @throws ComponentInfoAdapterException If an error occurs during parsing.
   */
  @Override
  public ScancodeComponentInfo parse(JsonNode scancodeJson, double licenseToTextRatioToTakeCompleteFile)
      throws ComponentInfoAdapterException {

    return parse(scancodeJson, licenseToTextRatioToTakeCompleteFile, "license_detections", this);
  }

  /**
   * Reformats and updates license information with effective data for Scancode version 32.
   *
   * @param license JSON node containing the license information.
   * @param effective The effective license data.
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

    String spdxId = null;
    String licenseDefaultUrl = null;
    double score = 0.0;
    int startLine = 0;
    int endLine = Integer.MAX_VALUE;

    for (JsonNode matche : license.get("matches")) {
      if (matche.get("rule_identifier").toString().contains("mit.LICENSE")) {
        spdxId = matche.get("spdx_license_expression").toString();
        score = matche.get("score").asDouble();
        startLine = matche.get("start_line").asInt();
        endLine = matche.get("end_line").asInt();
        licenseDefaultUrl = matche.get("rule_url").toString().replace("\"", "").trim();
        break;
      }
    }

    if (spdxId == null)
      return;

    reformatData(license, effective, path, takeCompleteFile, spdxId, licenseDefaultUrl, score, startLine, endLine);

  }

  /**
   * Sets licenses for the provided JSON node and updates the component information.
   *
   * @param licenses JSON node containing license information.
   * @param path Path of the file in the Scancode data.
   */
  @Override
  public void setLicenses(JsonNode licenses, String path) {

    if (licenses == null) {
      LOG.error("Licenses node is null");
      return;
    }

    for (JsonNode li : licenses) {
      JsonNode matchesNode = li.get("matches");
      if (matchesNode == null) {
        LOG.error("Matches node is null for license: " + li.toString());
        continue;
      }

      for (JsonNode matche : matchesNode) {
        String ruleIdentifier = matche.has("rule_identifier") ? matche.get("rule_identifier").asText() : null;
        String matchedText = matche.has("matched_text") ? matche.get("matched_text").asText() : null;
        String spdxId = matche.has("spdx_license_expression") ? matche.get("spdx_license_expression").asText() : null;

        if (ruleIdentifier == null || matchedText == null || spdxId == null) {
          LOG.error("Required license fields are missing: ruleIdentifier=" + ruleIdentifier + ", matchedText="
              + matchedText + ", spdxId=" + spdxId);
          continue;
        }

        LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier,
            matchedText, spdxId, this.licenseCurations);
        if (effective == null) {
          // license finding to be REMOVED via finding
          continue;
        }
        if (effective.license != null || effective.url != null) {
          // license or url are altered due to curation, so set the status
          this.componentScancodeInfos.setDataStatus(DataStatusValue.CURATED);
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

  }

  /**
   * Sets the license status for the given JSON node and path.
   *
   * @param license JSON node containing license information.
   * @param path Path of the file in the Scancode data.
   * @return The effective license data, or null if no effective data is found.
   */
  @Override
  public NewLicenseData setStatus(JsonNode license, String path) {

    LicenseCuration.NewLicenseData effective = null;
    JsonNode matchesNode = license.get("matches");
    if (matchesNode == null || !matchesNode.isArray()) {
      LOG.error("Matches node is null or not an array for license: " + license.toString());
      return null;
    }

    for (JsonNode matche : matchesNode) {
      JsonNode ruleIdentifierNode = matche.get("rule_identifier");
      JsonNode matchedTextNode = matche.get("matched_text");
      JsonNode spdxIdNode = matche.get("spdx_license_expression");

      if (ruleIdentifierNode == null || matchedTextNode == null || spdxIdNode == null) {
        LOG.error("Required fields are missing: ruleIdentifier=" + ruleIdentifierNode + ", matchedText="
            + matchedTextNode + ", spdxId=" + spdxIdNode);
        continue;
      }

      String ruleIdentifier = ruleIdentifierNode.asText();
      String matchedText = matchedTextNode.asText();
      String spdxId = spdxIdNode.asText();

      effective = getEffectiveLicenseInfoWithCuration(path, ruleIdentifier, matchedText, spdxId, this.licenseCurations);
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
    }
    return effective;
  }

}
