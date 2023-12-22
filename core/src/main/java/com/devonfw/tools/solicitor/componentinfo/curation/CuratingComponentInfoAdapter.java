package com.devonfw.tools.solicitor.componentinfo.curation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapter;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.LicenseInfo;

/**
 * A {@link ComponentInfoAdapter} which takes filtered {@link ComponentInfo} data from the configuret
 * {@link FilteredComponentInfoProvider} and curates it via the given {@link ComponentInfoCurator}.
 *
 */
public class CuratingComponentInfoAdapter implements ComponentInfoAdapter {

  private FilteredComponentInfoProvider filteredComponentInfoProvider;

  private ComponentInfoCurator componentInfoCurator;

  /**
   * The constructor.
   *
   * @param filteredComponentInfoProvider the provider of the filtered {@link ComponentInfo} data
   * @param componentInfoCurator the curator to take
   */
  public CuratingComponentInfoAdapter(FilteredComponentInfoProvider filteredComponentInfoProvider,
      ComponentInfoCurator componentInfoCurator) {

    this.filteredComponentInfoProvider = filteredComponentInfoProvider;
    this.componentInfoCurator = componentInfoCurator;
  }

  /**
   * Retrieves the component information and curation for a package identified by the given package URL. Returns the
   * data as a {@link ComponentInfo} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @param curationDataSelector identifies which source should be used for the curation data. <code>null</code>
   *        indicates that the default should be used.
   * @return the data derived from the scancode results after applying any defined curation.
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  @Override
  public ComponentInfo getComponentInfo(String packageUrl, String curationDataSelector)
      throws ComponentInfoAdapterException {

    if (isFeatureActive()) {

      ComponentInfo componentInfo = this.filteredComponentInfoProvider.getComponentInfo(packageUrl,
          curationDataSelector);
      if (componentInfo == null || componentInfo.getComponentInfoData() == null) {
        return componentInfo;
      }
      componentInfo = this.componentInfoCurator.curate(componentInfo, curationDataSelector);

      componentInfo = checkForIssues(componentInfo);

      return componentInfo;

    } else {
      return null;
    }

  }

  /**
   * Checks for issues in the given {@link ComponentInfo}. Issues include licenses falling into a defined set of keys.
   *
   * @param componentInfo The component information to check for issues.
   * @return the component info with the status set to "WITH_ISSUES" if issues are found.
   */
  private ComponentInfo checkForIssues(ComponentInfo componentInfo) {

    if (componentInfo.getComponentInfoData() == null) {
      return componentInfo;
    }

    Collection<? extends LicenseInfo> licenses = componentInfo.getComponentInfoData().getLicenses();
    if (licenses == null || licenses.isEmpty()) {
      return componentInfo;
    }
    boolean issueExisting = false;
    List<String> possibleIssues = Arrays.asList("LicenseRef-scancode-free-unknown");
    for (LicenseInfo li : licenses) {
      for (String key : possibleIssues) {
        if (key.equals(li.getSpdxid())) {
          issueExisting = true;
          break;
        }
      }
      if (issueExisting)
        break;
    }
    if (issueExisting) {
      DefaultComponentInfoImpl result = new DefaultComponentInfoImpl(componentInfo);
      result.setDataStatus(DataStatusValue.WITH_ISSUES);
      return result;
    } else {
      return componentInfo;
    }
  }

  /**
   * Returns if the adapter should be active. Default implementation returns <code>true</code>. Subclasses might
   * override this to enable/disable this functionality depending on some configuration;
   *
   * @return <code>true</code> if the adapter shall be active, <code>false</code> otherwise.
   */
  protected boolean isFeatureActive() {

    return true;
  }

}