package com.devonfw.tools.solicitor.componentinfo.curating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.DefaultComponentInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.DefaultLicenseInfoImpl;
import com.devonfw.tools.solicitor.componentinfo.curating.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curating.model.LicenseInfoCuration;

/**
 * This {@link ComponentInfoCurator} curates the {@link ComponentInfo} dat based on the data obtained from a
 * {@link SingleFileCurationProvider}.
 *
 */
@Component
public class ComponentInfoCuratorImpl implements ComponentInfoCurator {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentInfoCuratorImpl.class);

  private DirectUrlWebContentProvider contentProvider;

  private CurationProvider curationProvider;

  /**
   * The constructor.
   *
   * @param curationProvider the curation provider used to get the curations
   * @param contentProvider the content provider used for loading referenced file data
   */
  @Autowired
  public ComponentInfoCuratorImpl(CurationProvider curationProvider, DirectUrlWebContentProvider contentProvider) {

    this.curationProvider = curationProvider;
    this.contentProvider = contentProvider;
  }

  /**
   * Checks for the existence of curations for the given package via the {@link CurationProvider}. If curations exist
   * then the a new curated {@link ComponentInfo} instance will be created from the incoming uncurated
   * {@link ComponentInfo} and the curations.
   *
   * @param packageUrl the identifier of the package
   * @param componentInfo the componentInfo to curate
   * @return the curated component info
   * @throws ComponentInfoAdapterException if the curations could not be read
   */
  @Override
  public ComponentInfo curate(String packageUrl, ComponentInfo componentInfo) throws ComponentInfoAdapterException {

    ComponentInfoCuration foundCuration = this.curationProvider.findCurations(packageUrl);
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
      componentInfo.clearCopyrights();
      for (String copyright : curation.getCopyrights()) {
        componentInfo.addCopyright(copyright);
      }
    }
    if (curation.getUrl() != null) {
      componentInfo.setSourceRepoUrl(curation.getUrl());
    }
    if (curation.getLicenses() != null) {
      componentInfo.clearLicenses();
      for (LicenseInfoCuration licenseCuration : curation.getLicenses()) {
        String license = licenseCuration.getLicense();
        String url = licenseCuration.getUrl();
        String givenLicenseText = null;
        if (url != null && url.startsWith("file:")) {
          givenLicenseText = this.contentProvider.getContentForUri(url).getContent();
        }
        DefaultLicenseInfoImpl licenseInfo = new DefaultLicenseInfoImpl();
        licenseInfo.setSpdxId(license);
        licenseInfo.setLicenseFilePath(url);
        licenseInfo.setGivenLicenseText(givenLicenseText);
        componentInfo.addLicense(licenseInfo);
      }
    }
  }

}
