package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;

/**
 * A {@link ComponentInfoCurator} curates the {@link ComponentInfo} data.
 *
 */
public interface ComponentInfoCurator {

  /**
   * Curates the given {@link ComponentInfo}.
   *
   * @param componentInfo the componentInfo to curate
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return the curated component info or - if no curation are done - the incoming object
   * @throws ComponentInfoAdapterException if the curation could not be read
   */
  ComponentInfo curate(ComponentInfo componentInfo, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException;

}