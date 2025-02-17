package com.devonfw.tools.solicitor.componentinfo.scancode;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoProvider;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.packageurl.PackageURL;

/**
 * A {@link ComponentInfoProvider} which provides filtered {@link ComponentInfo}s based on Scancode data of a specific
 * scancode tool version.
 *
 */
public interface FilteredScancodeVersionComponentInfoProvider {

  /**
   * Checks if the given tool version is accepted by this provider.
   *
   * @param toolVersion the version of the tool
   * @return true if the version is accepted, false otherwise
   */
  boolean accept(String toolVersion);

  /**
   * Retrieves the component information for a package identified by the given package URL.
   *
   * @param packageUrl the identifier of the package for which information is requested
   * @param curationDataHandle identifies which source should be used for the curation data
   * @param rawScancodeData the raw scancode data
   * @param scancodeJson the parsed scancode JSON
   * @return the data for the component
   * @throws ComponentInfoAdapterException if there was an exception when reading the data
   * @throws CurationInvalidException if the curation data is not valid
   */
  ComponentInfo getComponentInfo(PackageURL packageUrl, CurationDataHandle curationDataHandle,
      ScancodeRawComponentInfo rawScancodeData, JsonNode scancodeJson)
      throws ComponentInfoAdapterException, CurationInvalidException;
}