/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.gradle;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link Reader} which reads data generated by the <a href="https://github.com/jk1/Gradle-License-Report">Gradle
 * License Report Plugin</a>.
 */
@Component
public class GradleLicenseReportReader extends AbstractReader implements Reader {

  /**
   * The supported type of this {@link Reader}.
   */
  public static final String SUPPORTED_TYPE = "gradle-license-report-json";

  /** {@inheritDoc} */
  @Override
  public Set<String> getSupportedTypes() {

    return Collections.singleton(SUPPORTED_TYPE);
  }

  /** {@inheritDoc} */
  @Override
  public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
      String repoType, String packageType, Map<String, String> configuration) {

    int components = 0;
    int licenses = 0;

    // According to tutorial https://github.com/FasterXML/jackson-databind/
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      Map<String, List> report = mapper.readValue(this.inputStreamFactory.createInputStreamFor(sourceUrl), Map.class);
      List<Map<String, String>> dependencies = report.get("dependencies");

      // Extract groupId and artifactId
      for (Map<String, String> dependency : dependencies) {
        String[] dependencyParts = dependency.get("moduleName").split(":");
        if (dependencyParts.length != 2) {
          throw new SolicitorRuntimeException(
              "Could not extract groupId, artifactId from moduleName: '" + dependency.get("moduleName") + "'");
        }

        ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
        appComponent.setApplication(application);
        appComponent.setGroupId(dependencyParts[0]);
        appComponent.setArtifactId(dependencyParts[1]);
        appComponent.setVersion(dependency.get("moduleVersion"));

        // Extract the first element from moduleUrls if available
        Object urlsObject = dependency.get("moduleUrls");
        if (urlsObject instanceof List<?>) {
          List<?> urlsList = (List<?>) urlsObject;
          if (!urlsList.isEmpty() && urlsList.get(0) instanceof String) {
            appComponent.setOssHomepage((String) urlsList.get(0));
          }
        }

        appComponent.setUsagePattern(usagePattern);
        appComponent.setRepoType(repoType);
        appComponent.setPackageUrl(PackageURLHelper
            .fromMavenCoordinates(dependencyParts[0], dependencyParts[1], dependency.get("moduleVersion")).toString());

        // Extract and process moduleLicenses
        Object licensesObject = dependency.get("moduleLicenses");
        if (licensesObject instanceof List<?>) {
          List<?> licensesList = (List<?>) licensesObject;
          for (Object licenseObject : licensesList) {
            if (licenseObject instanceof Map<?, ?>) {
              Map<?, ?> licenseMap = (Map<?, ?>) licenseObject;
              String licenseName = (String) licenseMap.get("moduleLicense");
              String licenseUrl = (String) licenseMap.get("moduleLicenseUrl");
              addRawLicense(appComponent, licenseName, licenseUrl, sourceUrl);
            }
          }
        }

        components++;
        licenses++;
      }

      doLogging(sourceUrl, application, components, licenses);

    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not read Gradle License Report inventory source '" + sourceUrl + "'",
          e);
    }
  }

}