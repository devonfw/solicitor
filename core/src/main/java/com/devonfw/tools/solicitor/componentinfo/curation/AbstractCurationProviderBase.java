package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;

/**
 * Abstract base implementation of {@link CurationProvider} to be used as starting point for concrete implementations.
 *
 */
public abstract class AbstractCurationProviderBase implements CurationProvider {

  /**
   * The constructor.
   */
  public AbstractCurationProviderBase() {

    super();
  }

  /**
   * Fetches curation data for a given package URL and returns it as a ComponentInfoCuration object.
   * <p>
   * Method to be implemented in subclasses.
   *
   * It has the same functionality as {@link CurationProvider#findCurations(String, CurationDataHandle)} but does not
   * require validating the fetched curation data.
   *
   * @param packageUrl The package URL for which curation data is to be fetched.
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return A ComponentInfoCuration object containing curation data.
   * @throws ComponentInfoAdapterException If any errors occur during the retrieval or parsing of curation data.
   * @throws ComponentInfoAdapterNonExistingCurationDataSelectorException If the specified curation data selector does
   *         not exist.
   */
  protected abstract ComponentInfoCuration doFindCurations(String packageUrl, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException;

  /**
   * Implementation of {@link CurationProvider#findCurations(String, CurationDataHandle)} which delegates the fetching
   * of the curations to (abstract) method {@link #doFindCurations(String, CurationDataHandle)} and then validates the
   * result before returning.
   */
  @Override
  public ComponentInfoCuration findCurations(String packageUrl, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException,
      CurationInvalidException {

    ComponentInfoCuration curation = doFindCurations(packageUrl, curationDataHandle);
    if (curation != null) {
      curation.validate();
    }
    return curation;
  }

}