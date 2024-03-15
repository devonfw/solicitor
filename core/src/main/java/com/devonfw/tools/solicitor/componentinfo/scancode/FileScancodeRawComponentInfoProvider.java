package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Provider for {@link ScancodeRawComponentInfo} which reads the scancode (and supplementary data) from the file system.
 *
 */
@Component
public class FileScancodeRawComponentInfoProvider implements ScancodeRawComponentInfoProvider {

  /**
   * The directory within the component root directory which contains the sources / the content
   */
  private static final String SOURCES_DIR = "sources/";

  private static final Logger LOG = LoggerFactory.getLogger(FileScancodeRawComponentInfoProvider.class);

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  private String repoBasePath;

  private AllKindsPackageURLHandler packageURLHandler;

  /**
   * The constructor.
   *
   * @param packageURLHandler handler to deal with PackageURLs.
   */
  @Autowired
  public FileScancodeRawComponentInfoProvider(AllKindsPackageURLHandler packageURLHandler) {

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

  /**
   * Retrieve the {@link ScancodeRawComponentInfo} for the package given by its PackageURL.
   *
   * @param packageUrl the identifier for the package
   * @return the raw data base on scancode and supplemental data. <code>null</code> if no data is available.
   * @throws ComponentInfoAdapterException is something unexpected happens
   * @throws ScancodeProcessingFailedException if no data can be provided due to previous issues when
   *         downloading/scanning the package data.
   */
  @Override
  public ScancodeRawComponentInfo readScancodeData(String packageUrl)
      throws ComponentInfoAdapterException, ScancodeProcessingFailedException {

    String packagePathPart = this.packageURLHandler.pathFor(packageUrl);
    String path = IOHelper.secureFilePath(this.repoBasePath, packagePathPart, "scancode.json");

    File scanCodeFile = new File(path);
    if (!scanCodeFile.exists()) {
      LOG.debug("No Scancode info available for PackageURL '{}'", packageUrl);
      throwExceptionForDownloadOrScanningFailures(packagePathPart);
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
   * Check if the unavailability of data is caused by previous failures when downloading of scanning the package
   * sources. This is done by testing if the marker files which indicate such failures exist.
   *
   * @param packagePathPart part of the file path to the data
   * @throws ScancodeProcessingFailedException if failures when downloading or scanning were detected
   */
  private void throwExceptionForDownloadOrScanningFailures(String packagePathPart)
      throws ScancodeProcessingFailedException {

    // Check if "sources.failed" exists
    String sourcesFailedPath = IOHelper.secureFilePath(this.repoBasePath, packagePathPart, "sources.failed");
    File sourcesFailedFile = new File(sourcesFailedPath);
    if (sourcesFailedFile.exists()) {
      throw new ScancodeProcessingFailedException("Downloading of package sources had failed.");
    }
    // Check if "scancodeScan.failed" exists
    String scancodeScanFailedPath = IOHelper.secureFilePath(this.repoBasePath, packagePathPart, "scancodeScan.failed");
    File scancodeScanFailedFile = new File(scancodeScanFailedPath);
    if (scancodeScanFailedFile.exists()) {
      throw new ScancodeProcessingFailedException("Scanning of package sources had failed.");
    }
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
    String path = IOHelper.secureFilePath(this.repoBasePath, packagePathPart, "origin.yaml");

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

  @Override
  public String retrieveContent(String packageUrl, String fileUri) {

    if (!fileUri.startsWith(PKG_CONTENT_SCHEMA_PREFIX)) {
      // we only handle pkgcontent: URIs here!
      return null;
    }
    if (fileUri.contains("..")) {
      // prevent directory traversal (there are other measures to also prevent this, but lets do it here explicitly)
      LOG.debug("Suspicious file traversal in URI '{}', returning null", fileUri);
      return null;
    }
    String pkgContentUriWithoutPrefix = fileUri.substring(PKG_CONTENT_SCHEMA_PREFIX.length());

    String relativeFilePathAndName = pkgContentUriWithoutPrefix;
    String lineInfo = null;
    int startOfLineInfo = pkgContentUriWithoutPrefix.indexOf("#L");
    if (startOfLineInfo >= 0) {
      lineInfo = pkgContentUriWithoutPrefix.substring(startOfLineInfo, pkgContentUriWithoutPrefix.length());
      relativeFilePathAndName = pkgContentUriWithoutPrefix.substring(0, startOfLineInfo);
    }

    String fullFilePathAndName = IOHelper.secureFilePath(this.repoBasePath, this.packageURLHandler.pathFor(packageUrl),
        SOURCES_DIR, relativeFilePathAndName);
    File file = new File(fullFilePathAndName);
    try (InputStream is = new FileInputStream(file); Scanner s = new Scanner(is)) {
      s.useDelimiter("\\A");
      String result = s.hasNext() ? s.next() : "";

      return MultilineHelper.possiblyExtractLines(result, lineInfo);
    } catch (IOException e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not retrieve content from file '" + fullFilePathAndName + "'", e);
      }
      LOG.info(LogMessages.FAILED_READING_FILE.msg(), fullFilePathAndName, e.getClass().getSimpleName());
    }
    return null;
  }

  @Override
  public boolean isLocalContentPath(String packageUrl, String path) {

    return (path != null && path.startsWith(SOURCES_DIR));
  }

  @Override
  public String pkgContentUriFromPath(String packageUrl, String path) {

    if (!isLocalContentPath(packageUrl, path)) {
      throw new IllegalArgumentException("'" + path + "' is not a valid path to content within the package");
    }
    return PKG_CONTENT_SCHEMA_PREFIX + path.substring(SOURCES_DIR.length());

  }
}
