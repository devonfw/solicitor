package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * TODO ohecker This type ...
 *
 */
@Component
public class ScancodeResultProvider {

  private static final Logger LOG = LoggerFactory.getLogger(ScancodeResultProvider.class);

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private String repoBasePath;

  private AllKindsPackageURLHandler packageURLHandler;

  private DirectUrlWebContentProvider contentProvider;

  /**
   * The constructor.
   */
  @Autowired
  public ScancodeResultProvider(DirectUrlWebContentProvider contentProvider,
      AllKindsPackageURLHandler packageURLHandler) {

    this.contentProvider = contentProvider;
    this.packageURLHandler = packageURLHandler;

  }

  /**
   * Sets repoBasePath.
   *
   * @param repoBasePath new value of repoBasePath.
   */
  @Value("${solicitor.scancode.repo-base-path}")
  public void setRepoBasePath(String repoBasePath) {

    this.repoBasePath = repoBasePath;
  }

  public ScancodeRawComponentInfo readScancodeData(String packageUrl) throws ComponentInfoAdapterException {

    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);
    String path = this.repoBasePath + "/" + packagePathPart + "/scancode.json";

    File scanCodeFile = new File(path);
    if (!scanCodeFile.exists()) {
      LOG.debug("No Scancode info available for PackageURL '{}'", packageUrl);
      return null;
    }
    String scancodeString;
    LOG.debug("Found Scancode info for PackageURL '{}'", packageUrl);
    try (InputStream is = new FileInputStream(scanCodeFile)) {
      scancodeString = IOHelper.readStringFromInputStream(is);
    } catch (IOException e) {
      throw new ComponentInfoAdapterException("Could not read Scancode JSON", e);
    }

    ScancodeRawComponentInfo result = new ScancodeRawComponentInfo();
    result.rawScancodeResult = scancodeString;
    addOriginData(packageUrl, result);
    return result;
  }

  /**
   * Adds the data about the origin of the package which is (optionally) contained in file "origin.yaml"
   *
   * @param packageUrl the identifier of the package
   * @param componentScancodeInfos the componentScancodeInfos to add the origin data to
   * @throws ComponentInfoAdapterException if there was an error when reading the file
   */
  private void addOriginData(String packageUrl, ScancodeRawComponentInfo componentScancodeInfos)
      throws ComponentInfoAdapterException {

    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);
    String path = this.repoBasePath + "/" + packagePathPart + "/origin.yaml";

    File originFile = new File(path);
    if (!originFile.exists()) {
      LOG.debug("No origin info available for PackageURL '{}'", packageUrl);
      return;
    }
    LOG.debug("Found origin info for PackageURL '{}'", packageUrl);

    try (InputStream is = new FileInputStream(originFile)) {

      JsonNode originYaml = yamlMapper.readTree(is);

      String sourceDownloadUrl = originYaml.get("sourceDownloadUrl") != null
          ? originYaml.get("sourceDownloadUrl").asText()
          : null;
      String packageDownloadUrl = originYaml.get("packageDownloadUrl") != null
          ? originYaml.get("packageDownloadUrl").asText()
          : null;
      String note = originYaml.get("note") != null ? originYaml.get("note").asText() : null;
      if (note != null) {
        LOG.debug("Note: " + note);
      }

      componentScancodeInfos.sourceDownloadUrl = sourceDownloadUrl;
      componentScancodeInfos.packageDownloadUrl = packageDownloadUrl;

    } catch (IOException e) {
      throw new ComponentInfoAdapterException("Could not read origin.yaml", e);
    }
  }

  public String retrieveContent(String path) {

    return this.contentProvider.getContentForUri(path).getContent();
  }
}
