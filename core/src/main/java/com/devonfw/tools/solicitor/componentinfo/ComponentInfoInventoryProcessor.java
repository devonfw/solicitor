package com.devonfw.tools.solicitor.componentinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * some external data source , like a scancode file store. If license information is found then the license, copyright
 * and notice file information of the model will be replaced by the data obtained from this source.
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
   * Process a single {@link ApplicationComponent}.
   *
   * @param ac application component
   * @return processing statistics
   */
  private Statistics processApplicationComponent(ApplicationComponent ac) {

    Statistics statistics = new Statistics();
    statistics.componentsTotal = 1;

    if (ac.getPackageUrl() != null) {
      ComponentInfo componentInfo = null;
      try {
        for (ComponentInfoAdapter cia : this.componentInfoAdapters) {
          componentInfo = cia.getComponentInfo(ac.getPackageUrl());
          // stop querying further adapters if some info was returned
          if (componentInfo != null) {
            break;
          }
        }
      } catch (ComponentInfoAdapterException e) {
        throw new SolicitorRuntimeException("Exception when reading component info data source", e);
      }
      if (componentInfo != null) {
        statistics.componentsWithComponentInfo = 1;
        ac.removeAllRawLicenses();

        if (componentInfo.getNoticeFilePath() != null) {
          ac.setNoticeFileUrl(componentInfo.getNoticeFilePath());
        }

        for (LicenseInfo li : componentInfo.getLicenses()) {
          addRawLicense(ac, li.getSpdxid(), li.getLicenseFilePath(), ORIGIN_COMPONENTINFO);
        }
        String copyrights = String.join("\n", componentInfo.getCopyrights());
        ac.setCopyrights(copyrights);
        // check whether VendorUrl is included in input file or not
        if (componentInfo.getUrl() != null) {
          ac.setOssHomepage(componentInfo.getUrl());
        }
        // check whether Source Reop Url is included in input file or not
        if (componentInfo.getSourceRepoUrl() != null) {
          ac.setSourceRepoUrl(componentInfo.getSourceRepoUrl());
        }
      } else {
        // no ComponentInfos info found for ac
      }
    } else {
      // can this happen?
    }
    return statistics;
  }

  /**
   * Adds a {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} to the given
   * {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent}.
   *
   * @param appComponent a {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent} object.
   * @param name a {@link java.lang.String} object.
   * @param url a {@link java.lang.String} object.
   * @param origin a {@link java.lang.String} object.
   */
  public void addRawLicense(ApplicationComponent appComponent, String name, String url, String origin) {

    RawLicense mlic = this.modelFactory.newRawLicense();
    mlic.setApplicationComponent(appComponent);
    mlic.setDeclaredLicense(name);
    mlic.setLicenseUrl(url);
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
