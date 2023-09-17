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
   * @param curationDataSelector identifies which source should be used for the curation data. <code>null</code>
   *        indicates that the default should be used.
   * @return the curation data if it exists or <code>null</code> if no curation exist for the package.
   * @throws ComponentInfoAdapterException if something unexpected happens
   */
  ComponentInfoCuration findCurations(String packageUrl, String curationDataSelector)
      throws ComponentInfoAdapterException;

}