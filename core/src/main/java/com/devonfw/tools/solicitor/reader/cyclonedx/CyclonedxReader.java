package com.devonfw.tools.solicitor.reader.cyclonedx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLException;
import com.devonfw.tools.solicitor.common.packageurl.impl.DelegatingPackageURLHandlerImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.library.model.license.LicenseSet;

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

  @Autowired
  private DelegatingPackageURLHandlerImpl delegatingPackageURLHandler;

  public void setDelegatingPackageURLHandler(DelegatingPackageURLHandlerImpl delegatingPackageURLHandler) {

    this.delegatingPackageURLHandler = delegatingPackageURLHandler;
  }

  /** {@inheritDoc} */
  @Override
  public Set<String> getSupportedTypes() {

    return Collections.singleton(SUPPORTED_TYPE);
  }

  /** {@inheritDoc} */
  @Override
  public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
      String repoType, String packageType, Map<String, String> configuration) {

    int componentCount = 0;
    int licenseCount = 0;
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

          String groupId = componentNode.get("group").asText();
          String artifactId = componentNode.get("name").asText();
          String version = componentNode.get("version").asText();
          String purl = componentNode.get("purl").asText();

          // Fill appComponents
          ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
          appComponent.setApplication(application);
          componentCount++;

          appComponent.setGroupId(groupId);
          appComponent.setArtifactId(artifactId);
          appComponent.setVersion(version);
          appComponent.setUsagePattern(usagePattern);
          appComponent.setRepoType(repoType);

          // Fill purl
          try {
            // check if handler exists for the package type defined in purl
            if (!this.delegatingPackageURLHandler.pathFor(purl).isEmpty()) {
              appComponent.setPackageUrl(purl);
            }
          } catch (SolicitorPackageURLException ex) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Problem with PackageURL", ex);
            }
            LOG.warn(LogMessages.CYCLONEDX_UNSUPPORTED_PURL.msg(), purl);
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
                licenseCount++;
                addRawLicense(appComponent, licenseNode.get("expression").asText(), null, sourceUrl);
              }

              // Check for licenses
              if (licenseNode.has("license")) {
                // Declared License can be written either in "id" or "name" field. Prefer "id" as its written in SPDX
                // format.
                if (licenseNode.get("license").has("id")) {
                  if (licenseNode.get("license").has("url")) {
                    licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("id").asText(),
                        licenseNode.get("license").get("url").asText(), sourceUrl);
                  } else {
                    licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("id").asText(), null, sourceUrl);
                  }
                } else if (licenseNode.get("license").has("name")) {
                  if (licenseNode.get("license").has("url")) {
                    licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("name").asText(),
                        licenseNode.get("license").get("url").asText(), sourceUrl);
                  } else {
                    licenseCount++;
                    addRawLicense(appComponent, licenseNode.get("license").get("name").asText(), null, sourceUrl);
                  }
                }
              }
            }
          }
        }
      }
      doLogging(sourceUrl, application, componentCount, licenseCount);
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not read CycloneDx inventory source '" + sourceUrl + "'", e);
    }

  }

}
