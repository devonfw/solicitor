package com.devonfw.tools.solicitor.componentinfo.curation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.componentinfo.ComponentContentProvider;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.CurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.DataStatusValue;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.DefaultLicenseInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseInfoCuration;

/**
 * This {@link ComponentInfoCurator} curates the {@link ComponentInfo} dat based on the data obtained from a
 * {@link SingleFileCurationProvider}.
 *
 */
@Component
public class ComponentInfoCuratorImpl implements ComponentInfoCurator {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentInfoCuratorImpl.class);

  private ComponentContentProvider componentContentProvider;

  private CurationProvider curationProvider;

  /**
   * The constructor.
   *
   * @param curationProvider the curation provider used to get the curation
   * @param componentContentProvider the provider used for loading referenced subpath data from the component
   */
  @Autowired
  public ComponentInfoCuratorImpl(CurationProvider curationProvider,
      ComponentContentProvider componentContentProvider) {

    this.curationProvider = curationProvider;
    this.componentContentProvider = componentContentProvider;
  }

  /**
   * Checks for the existence of curation for the given package via the {@link CurationProvider}. If curations exist
   * then a new curated {@link ComponentInfo} instance will be created from the incoming filtered {@link ComponentInfo}
   * and the curation.
   *
   * @param componentInfo the componentInfo to curate
   * @param curationDataHandle identifies which source should be used for the curation data.
   * @return the curated component info
   * @throws ComponentInfoAdapterException if the curation could not be read
   * @throws CurationInvalidException if the curation data is not valid
   */
  @Override
  public ComponentInfo curate(ComponentInfo componentInfo, CurationDataHandle curationDataHandle)
      throws ComponentInfoAdapterException, CurationInvalidException {

    ComponentInfoCuration foundCuration = this.curationProvider.findCurations(componentInfo.getPackageUrl(),
        curationDataHandle);
    if (foundCuration != null) {
      DefaultComponentInfoImpl componentInfoImpl = new DefaultComponentInfoImpl(componentInfo);
      applyFoundCurations(componentInfoImpl, foundCuration);
      return componentInfoImpl;
    } else {
      return componentInfo;
    }
  }

  private void applyFoundCurations(DefaultComponentInfoImpl componentInfo, ComponentInfoCuration curation) {

    if (curation.getCopyrights() != null) {
      componentInfo.getComponentInfoData().clearCopyrights();
      for (String copyright : curation.getCopyrights()) {
        componentInfo.getComponentInfoData().addCopyright(copyright);
      }
      componentInfo.setDataStatus(DataStatusValue.CURATED);
    }
    if (curation.getUrl() != null) {
      componentInfo.getComponentInfoData().setSourceRepoUrl(curation.getUrl());
      componentInfo.setDataStatus(DataStatusValue.CURATED);
    }
    if (curation.getLicenses() != null) {
      componentInfo.getComponentInfoData().clearLicenses();
      for (LicenseInfoCuration licenseCuration : curation.getLicenses()) {
        String license = licenseCuration.getLicense();
        String url = licenseCuration.getUrl();
        String givenLicenseText = null;
        if (url != null) {
          givenLicenseText = this.componentContentProvider.retrieveContent(componentInfo.getPackageUrl(), url);
        }
        DefaultLicenseInfoImpl licenseInfo = new DefaultLicenseInfoImpl();
        licenseInfo.setSpdxId(license);
        licenseInfo.setLicenseUrl(url);
        licenseInfo.setGivenLicenseText(givenLicenseText);
        componentInfo.getComponentInfoData().addLicense(licenseInfo);
      }
      componentInfo.setDataStatus(DataStatusValue.CURATED);
    }
  }

}
