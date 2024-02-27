package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;

/**
 * A {@link ComponentInfoCurator} curates the {@link ComponentInfo} data.
 *
 */
public interface ComponentInfoCurator {

  /**
   * Curates the given {@link ComponentInfo}.
   *
   * @param componentInfo the componentInfo to curate
   * @param curationDataSelector identifies which source should be used for the curation data. If the value of
   *        curationDataSelector equals "none," no curations will be applied.
   * @return the curated component info or - if no curations are done - the incoming object
   * @throws ComponentInfoAdapterException if the curation could not be read
   */
  ComponentInfo curate(ComponentInfo componentInfo, String curationDataSelector) throws ComponentInfoAdapterException;

}