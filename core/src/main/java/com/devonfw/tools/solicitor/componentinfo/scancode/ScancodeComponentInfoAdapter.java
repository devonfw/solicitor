package com.devonfw.tools.solicitor.componentinfo.scancode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapter;
import com.devonfw.tools.solicitor.componentinfo.curation.ComponentInfoCurator;
import com.devonfw.tools.solicitor.componentinfo.curation.CuratingComponentInfoAdapter;

/**
 * Adapter for providing curated {@link ComponentInfo} based on Scancode data.
 */
@Component
@Order(ComponentInfoAdapter.DEFAULT_PRIO)
public class ScancodeComponentInfoAdapter extends CuratingComponentInfoAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ScancodeComponentInfoAdapter.class);

  private boolean featureFlag = false;

  private boolean featureLogged = false;

  /**
   * The constructor.
   *
   * @param uncuratedScancodeComponentInfoProvider provider for uncurated data originating from scancode data
   * @param componentInfoCurator the curator to use
   */
  @Autowired
  public ScancodeComponentInfoAdapter(UncuratedScancodeComponentInfoProvider uncuratedScancodeComponentInfoProvider,
      ComponentInfoCurator componentInfoCurator) {

    super(uncuratedScancodeComponentInfoProvider, componentInfoCurator);
  }

  /**
   * Sets the feature flag for activating/deactivating this feature.
   *
   * @param featureFlag the flag
   */
  @Value("${solicitor.feature-flag.scancode}")
  public void setFeatureFlag(boolean featureFlag) {

    this.featureFlag = featureFlag;
  }

  @Override
  protected boolean isFeatureActive() {

    if (!this.featureLogged) {
      if (this.featureFlag) {
        LOG.warn(LogMessages.SCANCODE_PROCESSOR_STARTING.msg());
      } else {
        LOG.info(LogMessages.SCANCODE_FEATURE_DEACTIVATED.msg());
      }
      this.featureLogged = true;
    }
    return this.featureFlag;
  }

}
