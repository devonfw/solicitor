package com.devonfw.tools.solicitor.componentinfo;

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
 * some external data source , e.g. a scancode file store. If license information is found then the license, copyright
 * and notice file information of the model will be replaced by the data obtained from this source.
 *
 */
@Component
@Order(InventoryProcessor.BEFORE_RULE_ENGINE)
public class ComponentInfoInventoryProcessor implements InventoryProcessor {

  private static class Statistics {
    public int componentsTotal;

    public int componentsWithScancodeInfos;

    public void add(Statistics statistics) {

      this.componentsTotal += statistics.componentsTotal;
      this.componentsWithScancodeInfos += statistics.componentsWithScancodeInfos;
    }
  }

  /**
   * Origin data for raw license objects created by this Class.
   */
  private static final String ORIGIN_SCANCODE = "scancode";

  private static final Logger LOG = LoggerFactory.getLogger(ComponentInfoInventoryProcessor.class);

  private boolean featureFlag = false;

  private ComponentInfoAdapter scancodeAdapter;

  private ModelFactory modelFactory;

  /**
   * Sets the feature flag for activating/deactivating this feature.
   *
   * @param featureFlag the flag
   */
  @Value("${solicitor.feature-flag.scancode}")
  public void setFeatureFlag(boolean featureFlag) {

    this.featureFlag = featureFlag;
  }

  /**
   * The constructor.
   */
  public ComponentInfoInventoryProcessor() {

  }

  @Override
  public void processInventory(ModelRoot modelRoot) {

    if (!this.featureFlag) {
      LOG.info(LogMessages.SCANCODE_FEATURE_DEACTIVATED.msg());
    } else {
      LOG.warn(LogMessages.SCANCODE_PROCESSOR_STARTING.msg());

      Statistics overall = new Statistics();
      for (Application application : modelRoot.getEngagement().getApplications()) {
        for (ApplicationComponent ac : application.getApplicationComponents()) {
          Statistics single = processApplicationComponent(ac);
          overall.add(single);
        }
      }
      LOG.info(LogMessages.SCANCODE_INFO_READ.msg(), overall.componentsWithScancodeInfos, overall.componentsTotal);
    }
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
      ComponentInfo componentScancodeInfos;
      try {
        componentScancodeInfos = this.scancodeAdapter.getComponentInfo(ac.getPackageUrl());
      } catch (ComponentInfoAdapterException e) {
        throw new SolicitorRuntimeException("Exception when reading scancode file", e);
      }
      if (componentScancodeInfos != null) {
        statistics.componentsWithScancodeInfos = 1;
        ac.removeAllRawLicenses();

        if (componentScancodeInfos.getNoticeFilePath() != null) {
          ac.setNoticeFileUrl(componentScancodeInfos.getNoticeFilePath());
        }

        for (LicenseInfo li : componentScancodeInfos.getLicenses()) {
          addRawLicense(ac, li.getSpdxid(), li.getLicenseFilePath(), ORIGIN_SCANCODE);
        }
        String copyrights = String.join("\n", componentScancodeInfos.getCopyrights());
        ac.setCopyrights(copyrights);
        // check whether VendorUrl is included in input file or not
        if (componentScancodeInfos.getUrl() != null) {
          ac.setOssHomepage(componentScancodeInfos.getUrl());
        }
      } else {
        // no scancode info found for ac
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
    trace = "+ Component/License info read from scancode information";

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
   * This method sets the field <code>scancodeAdapter</code>
   *
   * @param scancodeAdapter new value of <code>scancodeAdapter</code>.
   */
  @Autowired
  public void setScancodeAdapter(ComponentInfoAdapter scancodeAdapter) {

    this.scancodeAdapter = scancodeAdapter;
  }

}
