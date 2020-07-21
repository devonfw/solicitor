/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Checks if deprecated features are activated and does appropriate logging /
 * error handling
 */
@Component
public class DeprecationChecker {

    private static final String configProperty = "solicitor.deprecated-features-allowed=true";

    private static final Logger LOG = LoggerFactory.getLogger(DeprecationChecker.class);

    private boolean depreactedFeaturesAllowed = false;

    @Value("${solicitor.deprecated-features-allowed}")
    public void depreactedFeaturesAllowed(boolean depreactedFeaturesAllowed) {

        this.depreactedFeaturesAllowed = depreactedFeaturesAllowed;
        if (this.depreactedFeaturesAllowed) {
            LOG.info(LogMessages.DEPRECATIONS_ACTIVE.msg(), configProperty,
                    LogMessages.USING_DEPRECATED_FEATURE.label());
        }
    }

    /**
     * Checks if deprecated features are activated. If yes, then a WARN message
     * will be logged. If deprecated features are not activated then an
     * Exception will be thrown.
     * 
     * @param detailsString Details of the deprecated feature to be included in
     *        the log message
     * @throws SolicitorRuntimeException if deprecated features are not
     *         activated
     */
    public void check(String detailsString) {

        if (depreactedFeaturesAllowed) {
            LOG.warn(LogMessages.USING_DEPRECATED_FEATURE.msg(), detailsString);
        } else {
            LOG.error(LogMessages.UNAVAILABLE_DEPRECATED_FEATURE.msg(), detailsString, configProperty);
            throw new SolicitorRuntimeException("Deprecated feature unavailable");
        }
    }

}
