package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;

/**
 * Provides curation data for {@link ComponentInfo} data.
 *
 */
public interface CurationProvider {

  /**
   * Return the curation data for a given package.
   *
   * @param packageUrl identifies the package
   * @param gitBranch identifies the git branch (optional, default is "main")
   * @return the curation data if it exists or <code>null</code> if no curations exist for the package.
   * @throws ComponentInfoAdapterException if something unexpected happens
   */
  ComponentInfoCuration findCurations(String packageUrl, String gitBranch) throws ComponentInfoAdapterException;

}