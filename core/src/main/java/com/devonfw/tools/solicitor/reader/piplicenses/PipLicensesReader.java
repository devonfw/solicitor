/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.piplicenses;

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
 * A {@link Reader} which reads data generated by <a href="https://pypi.org/project/pip-licenses/">Pip License </a>
 */
@Component
public class PipLicensesReader extends AbstractReader implements Reader {

  /**
   * The supported type of this {@link Reader}.
   */
  public static final String SUPPORTED_TYPE = "pip";

  /** {@inheritDoc} */
  @Override
  public Set<String> getSupportedTypes() {

    return Collections.singleton(SUPPORTED_TYPE);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
      String repoType, Map<String, String> configuration) {

    int componentCount = 0;
    int licenseCount = 0;

    // According to tutorial https://github.com/FasterXML/jackson-databind/
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    try {
      List l = mapper.readValue(this.inputStreamFactory.createInputStreamFor(sourceUrl), List.class);
      for (int i = 0; i < l.size(); i++) {
        Map attributes = (Map) l.get(i);
        String name = (String) attributes.get("Name");
        String version = (String) attributes.get("Version");
        String repo = (String) attributes.get("URL");
        String path = (String) attributes.get("LicenseFile");
        String licenseUrl = estimateLicenseUrl(repo, path);
        String homePage = (String) attributes.get("URL");
        // String licenseText = (String) attributes.get("LicenseText");
        String license = (String) attributes.get("License-Metadata");

        ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
        appComponent.setApplication(application);
        componentCount++;
        appComponent.setArtifactId(name);
        appComponent.setVersion(version);
        appComponent.setUsagePattern(usagePattern);
        appComponent.setGroupId("");
        appComponent.setOssHomepage(homePage);
        appComponent.setRepoType(repoType);
        appComponent.setPackageUrl(PackageURLHelper.fromPyPICoordinates(name, version).toString());

        addRawLicense(appComponent, license, licenseUrl, sourceUrl);
      }
      doLogging(sourceUrl, application, componentCount, licenseCount);
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not read pip license inventory source '" + sourceUrl + "'", e);
    }

  }

  // estimates license location in github links based on local file location
  private String estimateLicenseUrl(String repo, String path) {

    if (repo == null || repo.isEmpty()) {
      return null;
    }
    if (path == null || path.isEmpty()) {
      return repo;
    }

    if (repo.contains("github.com")) {
      String licenseRelative = path.substring(path.lastIndexOf("\\") + 1);
      if (repo.endsWith("/")) {
        repo = repo.substring(0, repo.length() - 1);
      }
      if (repo.contains("github.com")) {
        repo = repo.replace("git://", "https://");
        repo = repo.replace("github.com", "raw.githubusercontent.com");
        repo = repo.concat("/master/" + licenseRelative);
        return repo;
      }
    }
    return repo;
  }

}
