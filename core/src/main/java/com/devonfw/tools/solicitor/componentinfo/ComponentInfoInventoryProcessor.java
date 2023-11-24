package com.devonfw.tools.solicitor.componentinfo;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.InventoryProcessor;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;

/**
 * An {@link InventoryProcessor} which looks up license information for the found application components / packages at
 * external data source by the use of {@link ComponentInfoAdapter}s. If license information is found then the license,
 * copyright and notice file information of the model will be replaced by the data obtained from this source.
 *
 */
@Component
@Order(InventoryProcessor.BEFORE_RULE_ENGINE)
public class ComponentInfoInventoryProcessor implements InventoryProcessor {

  private static class Statistics {
    public int componentsTotal;

    public int componentsWithComponentInfo;

    public void add(Statistics statistics) {

      this.componentsTotal += statistics.componentsTotal;
      this.componentsWithComponentInfo += statistics.componentsWithComponentInfo;
    }
  }

  /**
   * Origin data for raw license objects created by this Class. Due to compatibility reasons this is named "scancode"
   * even due to the fact that it might originate from other sources.
   */
  private static final String ORIGIN_COMPONENTINFO = "scancode";

  private static final Logger LOG = LoggerFactory.getLogger(ComponentInfoInventoryProcessor.class);

  private ComponentInfoAdapter[] componentInfoAdapters;

  private ModelFactory modelFactory;

  private String curationDataSelector;

  /**
   * Set the curationDataSelector.
   *
   * @param curationDataSelector the curationDataSelector to chose when getting curation data. An empty string will be
   *        stored as <code>null</code> null which results in the default origin being taken.
   */
  @Value("${solicitor.curationDataSelector}")
  public void setCurationDataSelector(String curationDataSelector) {

    if (curationDataSelector != null && curationDataSelector.isEmpty()) {
      this.curationDataSelector = null;
    } else {
      this.curationDataSelector = curationDataSelector;

    }
  }

  /**
   * The constructor.
   */
  public ComponentInfoInventoryProcessor() {

  }

  @Override
  public void processInventory(ModelRoot modelRoot) {

    Statistics overall = new Statistics();
    for (Application application : modelRoot.getEngagement().getApplications()) {
      for (ApplicationComponent ac : application.getApplicationComponents()) {
        Statistics single = processApplicationComponent(ac);
        overall.add(single);
      }
    }
    LOG.info(LogMessages.COMPONENT_INFO_READ.msg(), overall.componentsWithComponentInfo, overall.componentsTotal);
  }

  /**
   * Processes a single {@link ApplicationComponent} by looking up license information from an external data source,
   * such as a scancode file store. If license information is found, it updates the relevant properties of the
   * {@link ApplicationComponent} with the data obtained from this source. The method also handles cases when no license
   * information is found or when there is an error reading the component info data source.
   *
   * @param ac The {@link ApplicationComponent} to be processed.
   * @return A {@link Statistics} object representing the processing statistics.
   * @throws SolicitorRuntimeException If there is an exception when reading the component info data source.
   */
  // TODO: ohecker: refactor this method
  private Statistics processApplicationComponent(ApplicationComponent ac) {

    Statistics statistics = new Statistics();
    statistics.componentsTotal = 1;

    if (ac.getPackageUrl() != null) {
      // Try to get component information from the available ComponentInfoAdapters
      ComponentInfo componentInfoCandidate = null;
      ComponentInfo componentInfo = null;
      try {
        for (ComponentInfoProvider cia : this.componentInfoAdapters) {
          componentInfoCandidate = cia.getComponentInfo(ac.getPackageUrl(), this.curationDataSelector);
          if (componentInfoCandidate != null) {
            componentInfo = componentInfoCandidate;
            // stop querying further adapters if some info was returned
            if (componentInfo.getComponentInfoData() != null) {
              break;
            }
          }
        }
      } catch (ComponentInfoAdapterException e) {
        throw new SolicitorRuntimeException("Exception when reading component info data source", e);
      }
      if (componentInfo != null) {
        if (componentInfo.getComponentInfoData() != null) {
          statistics.componentsWithComponentInfo = 1;
          /////////////////// TODO ///////////////////////////////
          // Set dataStatus and traceabilityNotes of the ApplicationComponent
          ac.setDataStatus("DA:" + componentInfo.getDataStatus());
          // Format and set the traceabilityNotes in the ApplicationComponent
          String formattedTraceabilityNotes = formatTraceabilityNotes(componentInfo);
          ac.setTraceabilityNotes(formattedTraceabilityNotes);

          ComponentInfoData componentInfoData = componentInfo.getComponentInfoData();
          // Update the notice file URL and content if available
          if (componentInfoData.getNoticeFileUrl() != null) {
            ac.setNoticeFileUrl(componentInfoData.getNoticeFileUrl());
          }

          if (componentInfoData.getNoticeFileContent() != null) {
            ac.setNoticeFileContent(componentInfoData.getNoticeFileContent());
          }

          // Process licenses if available
          if (componentInfoData.getLicenses().size() > 0) {
            ac.removeAllRawLicenses();
            for (LicenseInfo li : componentInfoData.getLicenses()) {
              addRawLicense(ac, li.getSpdxid(), li.getLicenseUrl(), li.getGivenLicenseText(), ORIGIN_COMPONENTINFO);
            }
          } else {
            LOG.info(LogMessages.COMPONENTINFO_NO_LICENSES.msg(),
                (ac.getGroupId() != null ? ac.getGroupId() + "/" : "") + ac.getArtifactId() + "/" + ac.getVersion());
            for (RawLicense rl : ac.getRawLicenses()) {
              String trace = rl.getTrace() + System.lineSeparator()
                  + "+ ComponentInfo available but without license information - keeping data from Reader";
              rl.setTrace(trace);
            }
          }

          String copyrights = String.join("\n", componentInfoData.getCopyrights());
          ac.setCopyrights(copyrights);
          // check whether VendorUrl is included in input file or not
          if (componentInfoData.getHomepageUrl() != null) {
            ac.setOssHomepage(componentInfoData.getHomepageUrl());
          }
          // check whether Source ReopUrl is included in input file or not
          if (componentInfoData.getSourceRepoUrl() != null) {
            ac.setSourceRepoUrl(componentInfoData.getSourceRepoUrl());
          }

          // always overwrite the download URLs - even if componentInfo does not contain any data
          ac.setPackageDownloadUrl(componentInfoData.getPackageDownloadUrl());
          ac.setSourceDownloadUrl(componentInfoData.getSourceDownloadUrl());
        } else {
          // no adapter delivered data, set the status of the last queried adapter
          ac.setDataStatus("ND:" + componentInfo.getDataStatus());
        }
      } else {
        // all adapters disabled
        ac.setDataStatus("ND:DISABLED"); // TODO: check if this is the correct status
      }
    } else {
      // ac did not contain PackageUrl
      // TODO: can this happen?
    }
    return statistics;
  }

  /**
   * Formats the traceabilityNotes from the given {@link ComponentInfo} by concatenating to a single string using line
   * separators.
   *
   * @param componentInfo The {@link ComponentInfo} containing the traceabilityNotes.
   * @return A formatted {@link String} representing the traceabilityNotes, separated by the long separator.
   */
  public String formatTraceabilityNotes(ComponentInfo componentInfo) {

    Collection<String> traceabilityNotes = componentInfo.getTraceabilityNotes();
    if (traceabilityNotes != null && !traceabilityNotes.isEmpty()) {
      return String.join(System.lineSeparator(), traceabilityNotes);
    } else {
      return "";
    }
  }

  /**
   * Adds a {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} to the given
   * {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent}.
   *
   * @param appComponent a {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent} object.
   * @param name a {@link java.lang.String} object.
   * @param url a {@link java.lang.String} object.
   * @param givenLicenseText a {@link java.lang.String} object.
   * @param origin a {@link java.lang.String} object.
   */
  public void addRawLicense(ApplicationComponent appComponent, String name, String url, String givenLicenseText,
      String origin) {

    RawLicense mlic = this.modelFactory.newRawLicense();
    mlic.setApplicationComponent(appComponent);
    mlic.setDeclaredLicense(name);
    mlic.setLicenseUrl(url);
    mlic.setDeclaredLicenseContent(givenLicenseText);
    mlic.setOrigin(origin);
    String trace;
    trace = "+ Component/License info read from ComponentInfo data source";

    mlic.setTrace(trace);
  }

  /**
   * This method sets the field <code>modelFactory</code>.
   *
   * @param modelFactory the new value of the field modelFactory
   */
  @Autowired
  public void setModelFactory(ModelFactory modelFactory) {

    this.modelFactory = modelFactory;
  }

  /**
   * This method sets the field <code>componentInfoAdapters</code>
   *
   * @param componentInfoAdapters new value of <code>componentInfoAdapters</code>.
   */
  @Autowired
  public void setComponentInfoAdapters(ComponentInfoAdapter[] componentInfoAdapters) {

    this.componentInfoAdapters = componentInfoAdapters;
  }

}
