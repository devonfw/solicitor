package com.devonfw.tools.solicitor.componentinfo.curation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.SelectorCurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.github.packageurl.PackageURL;

/**
 * Abstract base implementation of {@link CurationProvider} which supports storing curations in a hierarchical structure
 * so that curations are not required to be defined for specific component versions but might be defined for groups of
 * components. The hierarchy is derived from the PackageURL, see {@link PackageURLHandler#pathFor(PackageURL)}. The
 * {@link CurationProvider#findCurations(PackageURL, CurationDataHandle)} method of this class requires the
 * {@link CurationDataHandle} to be a {@link SelectorCurationDataHandle}.
 *
 */
public abstract class AbstractHierarchicalCurationProvider extends AbstractCurationProviderBase {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractHierarchicalCurationProvider.class);

  private final AllKindsPackageURLHandler packageURLHandler;

  /**
   * The constructor.
   *
   * @param packageURLHandler the required packageURLHandler
   */
  public AbstractHierarchicalCurationProvider(AllKindsPackageURLHandler packageURLHandler) {

    super();
    this.packageURLHandler = packageURLHandler;
  }

  /**
   * Fetches curation data for a given package URL from a curation repository and returns it as a ComponentInfoCuration
   * object.
   *
   * <p>
   * This method fetches curation data for a given package URL from a Repository and returns it as a
   * ComponentInfoCuration object. Fetching from a hierarchy and merging the different curation fragments is supported.
   * In case that data is requested for a non default curationDataSelector which does not exist will result in an
   * exception to be thrown.
   *
   * @param packageUrl The package URL for which curation data is to be fetched.
   * @param curationDataHandle This has to be a {@link SelectorCurationDataHandle} and specifies the (alternative)
   *        location to take the curation data from. If the curationDataSelector is set to "none" then no curation will
   *        be returned.
   * @return A ComponentInfoCuration object containing curation data (unvalidated). <code>null</code> if no curation
   *         data exists or the curationDataSelector of the curationDataHandle is set to <code>"none"</code>.
   * @throws ComponentInfoAdapterException If any errors occur during the retrieval of curation data.
   * @throws ComponentInfoAdapterNonExistingCurationDataSelectorException If the specified curationDataSelector does not
   *         exist.
   */
  @Override
  protected ComponentInfoCuration doFindCurations(PackageURL packageUrl, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException {

    LOG.debug("Received packageUrl: '{}'", packageUrl);
    if (packageUrl == null) {
      LOG.error(LogMessages.EMPTY_PACKAGE_URL.msg());
      throw new ComponentInfoAdapterException("The package URL cannot be null or empty.");
    }

    String curationDataSelector = ((SelectorCurationDataHandle) curationDataHandle).getCurationDataSelector();
    // Check if curation should not be applied
    if ("none".equalsIgnoreCase(curationDataSelector)) {
      LOG.debug("Retrieving curation is disabled for packageUrl '{}' due to curationDataSelector having value 'none'",
          packageUrl);
      return null;
    }
    String effectiveCurationDataSelector;
    effectiveCurationDataSelector = determineEffectiveCurationDataSelector(curationDataSelector);
    validateEffectiveCurationDataSelector(effectiveCurationDataSelector);

    // Parse the package URL to get the path to the curation file in the curation repository
    String pathFromPackageUrl = this.packageURLHandler.pathFor(packageUrl);
    LOG.debug("Parsed package path: '{}'", pathFromPackageUrl);
    if (pathFromPackageUrl == null || pathFromPackageUrl.isEmpty()) {
      LOG.error(LogMessages.EMPTY_PACKAGE_PATH.msg());
      throw new ComponentInfoAdapterException(
          "The package path parsed from the package URL is null or empty for the package URL: " + packageUrl);
    }

    List<ComponentInfoCuration> data = null;
    try {
      data = fetchAllDataFromCurationRepository(effectiveCurationDataSelector, pathFromPackageUrl);
    } catch (CurationInvalidException e) {
      throw new ComponentInfoAdapterException("Error while mapping JSON content", e.getCause());
    }

    if (data.isEmpty()) {
      assureCurationDataSelectorAvailable(effectiveCurationDataSelector);
      LOG.debug("NO curations found in Curations Repository for '{}' with curationDataSelector '{}'", packageUrl,
          effectiveCurationDataSelector);
      return null;
    }
    LOG.debug("Curations found in Curations Repository for '{}' with curationDataSelector '{}'", packageUrl,
        effectiveCurationDataSelector);

    ComponentInfoCuration merged = mergeCurationData(data);
    return merged;
  }

  /**
   * Fetches a single curation object (or curation fragment) from the hierarchy for the defined component.
   * <p>
   * To be implemented by concrete subclasses.
   *
   * @param effectiveCurationDataSelector the effective curationDataSelector
   * @param pathFragmentWithinRepo the path of the curation data or fragment. This will be either the value returned by
   *        {@link PackageURLHandler#pathFor(PackageURL)} or a (trailing) subpath.
   * @return The found curation object (might be a fragment). <code>null</code> if nothing is defined.
   * @throws ComponentInfoAdapterException if there was any unforeseen error when trying to retrieve the curation
   *         object.
   * @throws CurationInvalidException if the curation data was malformed
   */
  protected abstract ComponentInfoCuration fetchCurationFromRepository(String effectiveCurationDataSelector,
      String pathFragmentWithinRepo) throws ComponentInfoAdapterException, CurationInvalidException;

  /**
   * Validate the effective curationDataSelector. This needs to check is syntactically correct and might not trigger any
   * unwanted effects e.g. like path traversal.
   * <p>
   * To be implemented by concrete subclasses.
   *
   * @param effectiveCurationDataSelector the curationDataSelector to be validated
   * @throws ComponentInfoAdapterNonExistingCurationDataSelectorException if validation fails
   */
  protected abstract void validateEffectiveCurationDataSelector(String effectiveCurationDataSelector)
      throws ComponentInfoAdapterNonExistingCurationDataSelectorException;

  /**
   * Determine the effective curationDataSelector. This specifically needs to handle the case that the value
   * <code>null</code> is given which needs to be interpreted as "take the default".
   * <p>
   * To be implemented by concrete subclasses.
   *
   * @param curationDataSelector the incoming value. Might be <code>null</code>.
   * @return the effective curationDataSelector. Must not be <code>null</code>.
   */
  protected abstract String determineEffectiveCurationDataSelector(String curationDataSelector);

  /**
   * Method which checks if the given effecticeCurationDataSelector actually exists (is defined). The method will be
   * called in case that no curations could be found to make sure that this was not due to a wrong value given for the
   * effectiveCurationDataSelector.
   * <p>
   * To be implemented by concrete subclasses.
   *
   * @param effectiveCurationDataSelector the value to check for existence
   * @throws ComponentInfoAdapterException in case that there was any unforeseen exception when trying to check the
   *         existence of the curationDataSelector.
   * @throws ComponentInfoAdapterNonExistingCurationDataSelectorException in case that the given
   *         effectiveCurationDataSelector does not exist
   */
  protected abstract void assureCurationDataSelectorAvailable(String effectiveCurationDataSelector)
      throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException;

  /**
   * Determine if curations should be fetched in a hierarchical manner or only for the concrete version of a component.
   * The default implementation returns <code>true</code>. Subclasses might override this if they do not support
   * hierarchies or want to make this configurable.
   *
   * @return <code>true</code> if the hierarchy should be evaluated, <code>false</code> otherwise,
   */
  protected boolean isHierarchyEvaluation() {

    return true;
  }

  /**
   * Merges the list of found ComponentInfoCuration objects into a resulting curation.
   *
   * @param data the list of curation (framents)
   * @return the resulting curation object
   */
  private ComponentInfoCuration mergeCurationData(List<ComponentInfoCuration> data) {

    ComponentInfoCuration mergedCuration = null;
    for (ComponentInfoCuration oneCurationData : data) {
      mergedCuration = CurationUtil.merge(mergedCuration, oneCurationData);
    }

    return mergedCuration;
  }

  /**
   * Iterates though the hierarchy (optionally) and fetches the curation data objects/fragments via
   * {@link #fetchCurationFromRepository(String, String)}. Returns them as a list with the most specific ones at the
   * end.
   *
   * @param effectiveCurationDataSelector the effective curationDataSelector
   * @param pathFromPackageUrl the path as determined from the PackageURL (see
   *        {@link PackageURLHandler#pathFor(PackageURL)})
   * @return the list of found curation objects/fragments. Sorting of list is with increasing priority (curations being
   *         less specific appear first).
   * @throws ComponentInfoAdapterException if there was some unforseen issue when accessing the coration repository
   * @throws CurationInvalidException if some curation object was malformed
   */
  private List<ComponentInfoCuration> fetchAllDataFromCurationRepository(String effectiveCurationDataSelector,
      String pathFromPackageUrl) throws ComponentInfoAdapterException, CurationInvalidException {

    List<ComponentInfoCuration> curationsData = new ArrayList<>();

    String[] pathElements;
    if (isHierarchyEvaluation()) {
      pathElements = pathFromPackageUrl.split("/");
    } else {
      // only take the complete path - do not go through hierarchy
      pathElements = new String[] { pathFromPackageUrl };
    }

    String effectivePath = null;

    for (String element : pathElements) {
      if (effectivePath == null) {
        effectivePath = element;
      } else {
        effectivePath = effectivePath + "/" + element;
      }
      ComponentInfoCuration data = fetchCurationFromRepository(effectiveCurationDataSelector, effectivePath);
      if (data != null) {
        curationsData.add(data);
        LOG.debug("Curations found on path '{}' within curation repo.", effectivePath);
      } else {
        LOG.debug("No Curations found on path '{}' within curation repo.", effectivePath);
      }
    }
    return curationsData;
  }

}
