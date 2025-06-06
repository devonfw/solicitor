package com.devonfw.tools.solicitor.reader.cyclonedx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.packageurl.PackageURL;

/**
 * A {@link Reader} which reads data produced by the <a href="https://github.com/CycloneDX/cdxgen">CDXGEN Tool</a>.
 */
@Component
public class CyclonedxReader extends AbstractReader implements Reader {

  private static final Logger LOG = LoggerFactory.getLogger(CyclonedxReader.class);

  /**
   * The supported type of this {@link Reader}.
   */
  public static final String SUPPORTED_TYPE = "cyclonedx";

  /** {@inheritDoc} */
  @Override
  public Set<String> getSupportedTypes() {

    return Collections.singleton(SUPPORTED_TYPE);
  }

  /** {@inheritDoc} */
  @Override
  public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
      String repoType, String packageType, Map<String, String> configuration) {

    ReaderStatistics statistics = new ReaderStatistics();
    InputStream is;
    try {
      is = this.inputStreamFactory.createInputStreamFor(sourceUrl);
    } catch (IOException e1) {
      throw new SolicitorRuntimeException("Could not open inventory source '" + sourceUrl + "' for reading", e1);
    }
    // According to tutorial https://github.com/FasterXML/jackson-databind/
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      JsonNode rootNode = mapper.readTree(is);

      // Get all components in SBOM
      JsonNode componentsNode = rootNode.get("components");
      if (componentsNode != null) {
        for (JsonNode componentNode : componentsNode) {

          String groupId = componentNode.get("group") != null ? componentNode.get("group").asText() : null;
          String artifactId = componentNode.get("name").asText();
          String version = componentNode.get("version") != null ? componentNode.get("version").asText() : null;
          String purl = componentNode.get("purl") != null ? componentNode.get("purl").asText() : null;

          // Fill appComponents
          ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
          statistics.readComponentCount++;

          appComponent.setGroupId(groupId);
          appComponent.setArtifactId(artifactId);
          appComponent.setVersion(version);
          appComponent.setUsagePattern(usagePattern);
          appComponent.setRepoType(repoType);

          // Fill purl
          if (purl != null && !purl.isEmpty()) {
            try {
              PackageURL packageURL = PackageURLHelper.fromString(purl);
              appComponent.setPackageUrl(packageURL);
            } catch (SolicitorMalformedPackageURLException ex) {
              if (LOG.isDebugEnabled()) {
                LOG.debug("Problem with PackageURL", ex);
              }
              LOG.warn(LogMessages.READER_PURL_MALFORMED.msg(), purl);
            }
          }

          if (!addComponentToApplicationIfNotFiltered(application, appComponent, configuration, statistics)) {
            // skip processing of licenses and proceed to next component if component is filtered out
            continue;
          }

          // Fill license information
          JsonNode licensesNode = componentNode.get("licenses"); // licenses

          // Case if no licenses field exists
          if (licensesNode == null) {
            addRawLicense(appComponent, null, null, sourceUrl);
          }
          // Case if licenses field exists but is empty
          else if (licensesNode != null && licensesNode.isEmpty()) {
            addRawLicense(appComponent, null, null, sourceUrl);
          }
          // Case if licenses field exists and contains expressions or licenses
          else if (licensesNode != null && licensesNode.isEmpty() == false) {
            for (JsonNode licenseNode : licensesNode) {
              // Check for expressions
              if (licenseNode.has("expression")) {
                statistics.licenseCount++;
                addRawLicense(appComponent, licenseNode.get("expression").asText(), null, sourceUrl);
              }

              // Check for licenses
              if (licenseNode.has("license")) {
                // Declared License can be written either in "id" or "name" field. Prefer "id" as its written in SPDX
                // format.
                if (licenseNode.get("license").has("id")) {
                  if (licenseNode.get("license").has("url")) {
                    statistics.licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("id").asText(),
                        licenseNode.get("license").get("url").asText(), sourceUrl);
                  } else {
                    statistics.licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("id").asText(), null, sourceUrl);
                  }
                } else if (licenseNode.get("license").has("name")) {
                  if (licenseNode.get("license").has("url")) {
                    statistics.licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("name").asText(),
                        licenseNode.get("license").get("url").asText(), sourceUrl);
                  } else {
                    statistics.licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("name").asText(), null, sourceUrl);
                  }
                }
              }
            }
          }
        }
      }
      doLogging(configuration, sourceUrl, application, statistics);
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not read CycloneDx inventory source '" + sourceUrl + "'", e);
    }

  }

}
