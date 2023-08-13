package com.devonfw.tools.solicitor.componentinfo.curating;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curating.model.ComponentInfoCuration;

/**
 * Provides curation data for {@link ComponentInfo} data.
 *
 */
public interface CurationProvider {

  /**
   * Return the curation data for a given package
   *
   * @param packageUrl identifies the package
   * @return the curation data if it exists or <code>null</code> if no curations exist for the package.
   * @throws ComponentInfoAdapterException if something unexpected happens
   */
  ComponentInfoCuration findCurations(String packageUrl) throws ComponentInfoAdapterException;

}