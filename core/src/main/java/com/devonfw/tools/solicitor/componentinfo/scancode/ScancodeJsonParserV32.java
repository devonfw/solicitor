package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.devonfw.tools.solicitor.componentinfo.ScancodeComponentInfoData;
import com.devonfw.tools.solicitor.componentinfo.curation.LicenseCuration;
import com.fasterxml.jackson.databind.JsonNode;

public class ScancodeJsonParserV32 {

  public void parseAndMapScancodeJsonV32(JsonNode file, ScancodeComponentInfoData scancodeComponentInfoData,
      String packageUrl, List<LicenseCuration> licenseCurations, boolean takeCompleteFile) {

    // special handling for Classpath-exception-2.0
    Map<String, String> spdxIdMap = new HashMap<>();
    boolean classPathExceptionExists = false;
    boolean isNewVersion = false;

    int numberOfGplLicenses = 0;
    JsonNode licenses = file.get("licenses");
    String licenseName = "", spdxId = "", ruleIdentifier = "", matchedText = "";
    if (licenses.isEmpty()) {
      isNewVersion = true;
      licenses = file.get("license_detections");
      spdxId = file.get("detected_license_expression_spdx").asText();
      for (JsonNode li : licenses) {
        for (JsonNode matche : li.get("matches")) {
          ruleIdentifier = matche.get("rule_identifier").asText();
          matchedText = matche.get("matched_text").asText();
          LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(packageUrl, ruleIdentifier,
              matchedText, spdxId, licenseCurations);
          if (effective == null) {
            // license finding to be REMOVED via finding
            continue;
          }
          licenseName = effective.license != null ? effective.license : spdxId;

          if ("Classpath-exception-2.0".equals(licenseName)) {
            classPathExceptionExists = true;
          }
          if (!spdxIdMap.containsKey(licenseName)) {
            spdxIdMap.put(licenseName, licenseName);
            if (licenseName.startsWith("GPL")) {
              numberOfGplLicenses++;
            }
          }
        }
      }
    } else {

      for (JsonNode li : licenses) {
        ruleIdentifier = li.get("matched_rule").get("identifier").asText();
        matchedText = li.get("matched_text").asText();
        spdxId = li.get("spdx_license_key").asText();

        LicenseCuration.NewLicenseData effective = getEffectiveLicenseInfoWithCuration(packageUrl, ruleIdentifier,
            matchedText, spdxId, licenseCurations);
        if (effective == null) {
          // license finding to be REMOVED via finding
          continue;
        }
        licenseName = effective.license != null ? effective.license : spdxId;

        if ("Classpath-exception-2.0".equals(licenseName)) {
          classPathExceptionExists = true;
        }
        if (!spdxIdMap.containsKey(licenseName)) {
          spdxIdMap.put(licenseName, licenseName);
          if (licenseName.startsWith("GPL")) {
            numberOfGplLicenses++;
          }
        }
      }

    }
    if (classPathExceptionExists) {
      if (numberOfGplLicenses == 0) {
        LOG.warn(LogMessages.CLASSPATHEXCEPTION_WITHOUT_GPL.msg(), packageUrl);
      } else if (numberOfGplLicenses > 1) {
        LOG.warn(LogMessages.CLASSPATHEXCEPTION_MULTIPLE_GPL.msg(), packageUrl);
      } else {
        LOG.debug("Adjusting GPL license to contain WITH Classpath-execption-2.0 for " + packageUrl);
        for (String licenseNameSpdx : spdxIdMap.keySet()) {
          if (licenseNameSpdx.startsWith("GPL")) {
            spdxIdMap.put(licenseNameSpdx, licenseNameSpdx + " WITH Classpath-exception-2.0");
          }
        }
        // do not output the Classpath-exception-2.0 as separate License
        spdxIdMap.remove("Classpath-exception-2.0");
      }
    }
  }

  private LicenseCuration.NewLicenseData getEffectiveLicenseInfoWithCuration(String path, String ruleIdentifier,
      String matchedText, String spdxId, List<LicenseCuration> licenseCurations) {

    LicenseCuration.NewLicenseData result = null;
    for (LicenseCuration licenseCuration : licenseCurations) {
      if (licenseCuration.getRule().equals(ruleIdentifier) && licenseCuration.getMatchedText().equals(matchedText)) {
        result = licenseCuration.getEffective();
        break;
      }
    }
    return result;
  }
}
