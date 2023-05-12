package com.devonfw.tools.solicitor.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.InventoryProcessor;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;

/**
 * An {@link InventoryProcessor} which fills the {@link ApplicationComponent#getPackageDownloadUrl()} and
 * {@link ApplicationComponent#getSourceDownloadUrl()} with default values derived from the PackageURL if there is no
 * other data yet.
 *
 */
@Component
@Order(InventoryProcessor.AFTER_READERS)
public class DefaultDownloadUrlInventoryProcessor implements InventoryProcessor {

  @Autowired
  private AllKindsPackageURLHandler packageURLHandler;

  /**
   * The constructor.
   */
  public DefaultDownloadUrlInventoryProcessor() {

  }

  @Override
  public void processInventory(ModelRoot modelRoot) {

    for (Application application : modelRoot.getEngagement().getApplications()) {
      for (ApplicationComponent ac : application.getApplicationComponents()) {
        String packageUrl = ac.getPackageUrl();
        if (packageUrl != null && !packageUrl.isEmpty()) {
          if (ac.getPackageDownloadUrl() == null) {
            ac.setPackageDownloadUrl(this.packageURLHandler.packageDownloadUrlFor(packageUrl));
          }
          if (ac.getSourceDownloadUrl() == null) {
            ac.setSourceDownloadUrl(this.packageURLHandler.sourceDownloadUrlFor(packageUrl));
          }

        }
      }
    }
  }

}
