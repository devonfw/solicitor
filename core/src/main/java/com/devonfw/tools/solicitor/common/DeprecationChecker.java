/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Checks if deprecated features are activated and does appropriate logging / error handling
 */
@Component
public class DeprecationChecker {

  private static final String configProperty = "solicitor.deprecated-features-allowed=true";

  private static final Logger LOG = LoggerFactory.getLogger(DeprecationChecker.class);

  private boolean deprecatedFeaturesAllowed = false;

  private Set<String> thrownWarnings = new HashSet<String>();

  @Value("${solicitor.deprecated-features-allowed}")
  public void setDeprecatedFeaturesAllowed(boolean deprecatedFeaturesAllowed) {

    this.deprecatedFeaturesAllowed = deprecatedFeaturesAllowed;
    if (this.deprecatedFeaturesAllowed) {
      LOG.info(LogMessages.DEPRECATIONS_ACTIVE.msg(), configProperty,
          LogMessages.USING_DEPRECATED_FEATURE_FORCED.label());
    }
  }

  /**
   * Checks if deprecated features are activated. If yes, then a WARN message will be logged. If deprecated features are
   * not activated then an Exception will be thrown.
   * 
   * @param warnOnly if set to <code>true</code> then there will be only a warning logged independently of whether
   *        deprecated features are activated or not. This is the first step when deprecating a feature.
   * @param detailsString Details of the deprecated feature to be included in the log message
   * @throws SolicitorRuntimeException if deprecated features are not activated
   */
  public void check(boolean warnOnly, String detailsString) {

    if (!thrownWarnings.contains(detailsString)) {
      if (warnOnly) {
        LOG.warn(LogMessages.USING_DEPRECATED_FEATURE.msg(), detailsString);
        thrownWarnings.add(detailsString);
      } else {
        if (deprecatedFeaturesAllowed) {
          LOG.warn(LogMessages.USING_DEPRECATED_FEATURE_FORCED.msg(), configProperty, detailsString);
          thrownWarnings.add(detailsString);
        } else {
          LOG.error(LogMessages.UNAVAILABLE_DEPRECATED_FEATURE.msg(), detailsString, configProperty);
          throw new SolicitorRuntimeException("Deprecated feature unavailable");
        }
      }
    }
  }

}
