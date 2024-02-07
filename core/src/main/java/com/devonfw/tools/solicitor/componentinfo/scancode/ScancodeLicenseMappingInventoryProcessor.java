package com.devonfw.tools.solicitor.componentinfo.scancode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.InventoryProcessor;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.RegexListPredicate;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.NormalizedLicenseImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.ruleengine.drools.ModelHelper;

/**
 * An {@link InventoryProcessor} which maps {@link RawLicense}s originating from {@link ComponentInfo} scancode
 * information to {@link NormalizedLicense} information.
 */
@Component
@Order(InventoryProcessor.BEFORE_RULE_ENGINE + 10)
public class ScancodeLicenseMappingInventoryProcessor implements InventoryProcessor {

  /**
   * Data structure for aggregating statistics.
   */
  public static class Statistics {
    int total;

    int skippedBlacklist;

    int mappedLicenseRef;

    int mappedSpdx;

    int mappedIgnore;

    int skippedUnknown;
  }

  /**
   * Prefix used for {@link ApplicationComponent#getDataStatus()} in case that data is available.
   */
  private static final String DA_STATUS_PREFIX = "DA:";

  /**
   * Prefix of licenses detected by scancode which do not correspond to a SPDX-ID.
   */
  private static final String LICENSEREF_PREFIX = "LicenseRef-scancode";

  /**
   * Origin data for raw license objects originating from scancode data.
   */
  private static final String ORIGIN_COMPONENTINFO = "scancode";

  private static final Logger LOG = LoggerFactory.getLogger(ScancodeLicenseMappingInventoryProcessor.class);

  private ModelFactory modelFactory;

  private RegexListPredicate mappingBlacklistPredicate = new RegexListPredicate();

  private RegexListPredicate mappingIgnorelistPredicate = new RegexListPredicate();

  private boolean featureFlag;

  /**
   * The constructor.
   */
  public ScancodeLicenseMappingInventoryProcessor() {

  }

  @Override
  public void processInventory(ModelRoot modelRoot) {

    if (isFeatureActive()) {
      Statistics stat = new Statistics();
      for (Application application : modelRoot.getEngagement().getApplications()) {
        for (ApplicationComponent ac : application.getApplicationComponents()) {
          processApplicationComponent(ac, stat);
        }
      }
      LOG.info(LogMessages.SCANCODE_MAPPING_STATISTICS.msg(), stat.total, stat.skippedBlacklist, stat.skippedUnknown,
          stat.mappedLicenseRef, stat.mappedSpdx, stat.mappedIgnore);
    }
  }

  /**
   * Processes a single {@link ApplicationComponent} and tries to automatically create a {@link NormalizedLicense}
   * object for all {@link RawLicense} objects which originate from scancode information.
   *
   * @param ac The {@link ApplicationComponent} to be processed.
   * @param stat An object which holds the aggregated processing statistics
   */
  protected void processApplicationComponent(ApplicationComponent ac, Statistics stat) {

    if (ac.getDataStatus() != null && ac.getDataStatus().startsWith(DA_STATUS_PREFIX)) {
      for (RawLicense rl : ac.getRawLicenses()) {
        if (ORIGIN_COMPONENTINFO.equals(rl.getOrigin()) && rl.getDeclaredLicense() != null) {
          stat.total++;
          String license = rl.getDeclaredLicense();
          if (isBlacklisted(license)) {
            stat.skippedBlacklist++;
            continue;
          }
          if (isToBeIgnored(license)) {
            stat.mappedIgnore++;
            NormalizedLicenseImpl nl = ((ModelFactoryImpl) this.modelFactory).newNormalizedLicense(rl);
            rl.setSpecialHandling(true);
            nl.setNormalizedLicense("Ignore");
            nl.setNormalizedLicenseType("IGNORE");
            nl.setNormalizedLicenseUrl(rl.getLicenseUrl());
            ModelHelper.appendTraceToNormalizedLicense(nl, "+ mapped detected Scancode license ref (" + license
                + ") to IGNORE/Ignore as it matches the given pattern list");
          } else if (license.startsWith(LICENSEREF_PREFIX)) {
            stat.mappedLicenseRef++;
            NormalizedLicenseImpl nl = ((ModelFactoryImpl) this.modelFactory).newNormalizedLicense(rl);
            rl.setSpecialHandling(true);
            nl.setNormalizedLicense(license);
            nl.setNormalizedLicenseType("SCANCODE");
            nl.setNormalizedLicenseUrl(rl.getLicenseUrl());
            ModelHelper.appendTraceToNormalizedLicense(nl,
                "+ mapped detected Scancode license ref (" + license + ") to SCANCODE/" + license);
          } else if (isSpdxLicensePossiblyWithException(license)) {
            stat.mappedSpdx++;
            NormalizedLicenseImpl nl = ((ModelFactoryImpl) this.modelFactory).newNormalizedLicense(rl);
            rl.setSpecialHandling(true);
            nl.setNormalizedLicense(license);
            nl.setNormalizedLicenseType("OSS-SPDX");
            nl.setNormalizedLicenseUrl(rl.getLicenseUrl());
            ModelHelper.appendTraceToNormalizedLicense(nl,
                "+ mapped license detected by Scancode (" + license + ") to OSS-SPDX/" + license);
          } else {
            stat.skippedUnknown++;
            LOG.warn(LogMessages.SCANCODE_NO_MAPPING.msg(), license);
          }
        }
      }
    }

  }

  /**
   * Checks if the given license id is blacklisted and should not be mapped automatically to a
   * {@link NormalizedLicense}.
   *
   * @param license the license id to check
   * @return <code>true</code> if the license id is blacklisted.
   */
  protected boolean isBlacklisted(String license) {

    return this.mappingBlacklistPredicate.test(license,
        "License id '{}' is blacklisted via regex '{}' and will not be mapped to a NormalizedLicense");
  }

  /**
   * Checks if the given license id shall be mapped to a {@link NormalizedLicense} with pseudo license id Ignore.
   *
   * @param license the license id to check
   * @return <code>true</code> if the license id matches the ignore list.
   */
  protected boolean isToBeIgnored(String license) {

    return this.mappingIgnorelistPredicate.test(license,
        "License id '{}' matches ignore list via regex '{}' and "
            + "will be mapped to a NormalizedLicense using pseudo license Ignore");
  }

  /**
   * Checks if the argument represents a SPDX-ID for a license or a license and an Exception, both given by their
   * SPDX-IDs
   *
   * @param license the license string to check
   * @return <code>true</code> if this is a license (with optional Exception)
   */
  protected boolean isSpdxLicensePossiblyWithException(String license) {

    if (license == null) {
      return false;
    }
    if (license.contains("WITH")) {
      String[] parts = license.split("WITH");
      if (parts.length != 2) {
        return false;
      }
      String pureLicense = parts[0].trim();
      String exception = parts[1].trim();
      return LicenseInfoFactory.isSpdxListedLicenseId(pureLicense)
          && LicenseInfoFactory.isSpdxListedExceptionId(exception);
    } else {
      return LicenseInfoFactory.isSpdxListedLicenseId(license);
    }

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
   * Sets the blacklist of license ids which will not be attempted to be mapped automatically.
   *
   * @param licenseIdMappingBlacklistRegexes an array of regular expressions which define a blacklist of license ids
   *        which will not be mapped automatically to {@link NormalizedLicense} information.
   */
  @Value("${solicitor.scancode.automapping.blacklist}")
  public void setLicenseIdMappingBlacklistRegexes(String[] licenseIdMappingBlacklistRegexes) {

    this.mappingBlacklistPredicate.setRegexes(licenseIdMappingBlacklistRegexes);
  }

  /**
   * Sets the list of license ids which will be mapped to {@link NormalizedLicense} using IGNORE/Ignore.
   *
   * @param licenseIdMappingIgnorelistRegexes an array of regular expressions which define a list of license ids which
   *        will be mapped to @link NormalizedLicense} information using type/pseudolicense IGNORE/Ignore.
   */
  @Value("${solicitor.scancode.automapping.ignorelist}")
  public void setLicenseIdMappingIgnorelistRegexes(String[] licenseIdMappingIgnorelistRegexes) {

    this.mappingIgnorelistPredicate.setRegexes(licenseIdMappingIgnorelistRegexes);

  }

  /**
   * Sets the feature flag for activating/deactivating this feature.
   *
   * @param featureFlag the flag
   */
  @Value("${solicitor.feature-flag.scancode.automapping}")
  public void setFeatureFlag(boolean featureFlag) {

    this.featureFlag = featureFlag;
  }

  /**
   * Check if the feature flag is set and log the result.
   *
   * @return the value of the feature flag
   */
  protected boolean isFeatureActive() {

    if (this.featureFlag) {
      LOG.info(LogMessages.SCANCODE_AUTOMAPPING_STARTED.msg(), this.mappingBlacklistPredicate.getRegexesAsString(),
          this.mappingIgnorelistPredicate.getRegexesAsString());
    } else {
      LOG.info(LogMessages.SCANCODE_AUTOMAPPING_FEATURE_DEACTIVATED.msg());
    }
    return this.featureFlag;
  }

}
