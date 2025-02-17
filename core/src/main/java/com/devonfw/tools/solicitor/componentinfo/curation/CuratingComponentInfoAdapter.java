package com.devonfw.tools.solicitor.componentinfo.curation;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.RegexListPredicate;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapter;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.LicenseInfo;
import com.github.packageurl.PackageURL;

/**
 * A {@link ComponentInfoAdapter} which takes filtered {@link ComponentInfo} data from the configuret
 * {@link FilteredComponentInfoProvider} and curates it via the given {@link ComponentInfoCurator}.
 *
 */
public class CuratingComponentInfoAdapter implements ComponentInfoAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(CuratingComponentInfoAdapter.class);

  private FilteredComponentInfoProvider filteredComponentInfoProvider;

  private ComponentInfoCurator componentInfoCurator;

  private RegexListPredicate licenseIdIssuesPredicate = new RegexListPredicate();

  private boolean regexesLogged = false;

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
   * @param curationDataHandle Identifies which source should be used for the curation data.
   * @return the data derived from the scancode results after applying any defined curation.
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   * @throws CurationInvalidException if the curation data is not valid
   */
  @Override
  public ComponentInfo getComponentInfo(PackageURL packageUrl, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, CurationInvalidException {

    if (isFeatureActive()) {

      ComponentInfo componentInfo = this.filteredComponentInfoProvider.getComponentInfo(packageUrl, curationDataHandle);
      if (componentInfo == null || componentInfo.getComponentInfoData() == null) {
        return componentInfo;
      }
      componentInfo = this.componentInfoCurator.curate(componentInfo, curationDataHandle);

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
    for (LicenseInfo li : licenses) {
      if (isIssue(li.getSpdxid())) {
        issueExisting = true;
        break;
      }
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

  /**
   * Checks if the given license id falls in the category of "WITH_ISSUES".
   *
   * @param license the license id to check
   * @return <code>true</code> if the license id matches the issue list.
   */
  protected boolean isIssue(String license) {

    if (!this.regexesLogged) {
      LOG.info(LogMessages.SCANCODE_ISSUE_DETECTION_REGEX.msg(), this.licenseIdIssuesPredicate.getRegexesAsString());
      this.regexesLogged = true;
    }
    return this.licenseIdIssuesPredicate.test(license,
        "License id '{}' matches issue list via regex '{}' and result will be set to status WITH_ISSUES");
  }

  /**
   * Sets the list of license ids which will be regarded as "WITH_ISSUES"
   *
   * @param licenseIdIssuesRegexes an array of regular expressions which define a the patterns of license ids which will
   *        be regarded as "WITH_ISSUES".
   */
  @Value("${solicitor.scancode.issuelistpatterns}")
  public void setLicenseIdIssuesRegexes(String[] licenseIdIssuesRegexes) {

    this.licenseIdIssuesPredicate.setRegexes(licenseIdIssuesRegexes);
  }

}