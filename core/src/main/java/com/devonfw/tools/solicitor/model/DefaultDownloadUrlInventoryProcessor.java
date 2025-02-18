package com.devonfw.tools.solicitor.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.InventoryProcessor;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLUnavailableOperationException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.github.packageurl.PackageURL;

/**
 * An {@link InventoryProcessor} which fills the {@link ApplicationComponent#getPackageDownloadUrl()} and
 * {@link ApplicationComponent#getSourceDownloadUrl()} with default values derived from the PackageURL if there is no
 * other data yet.
 *
 */
@Component
@Order(InventoryProcessor.AFTER_READERS)
public class DefaultDownloadUrlInventoryProcessor implements InventoryProcessor {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultDownloadUrlInventoryProcessor.class);

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
        PackageURL packageUrl = ac.getPackageUrl();
        if (packageUrl != null) {
          if (ac.getPackageDownloadUrl() == null) {
            try {
              ac.setPackageDownloadUrl(this.packageURLHandler.packageDownloadUrlFor(packageUrl));
            } catch (SolicitorPackageURLUnavailableOperationException e) {
              LOG.info(LogMessages.NO_PACKAGE_DOWNLOAD_URL_FOR_PACKAGEURL.msg(), packageUrl);
            }
          }
          if (ac.getSourceDownloadUrl() == null) {
            try {
              ac.setSourceDownloadUrl(this.packageURLHandler.sourceDownloadUrlFor(packageUrl));
            } catch (SolicitorPackageURLUnavailableOperationException e) {
              LOG.info(LogMessages.NO_SOURCE_DOWNLOAD_URL_FOR_PACKAGEURL.msg(), packageUrl);
            }
          }

        }
      }
    }
  }

}
