/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;

public class CachingLicenseContentProviderImplTests {

  private static final Logger LOG = LoggerFactory.getLogger(CachingLicenseContentProviderImplTests.class);

  @Test
  @Disabled
  public void readFile() {

    DirectUrlWebContentProvider licenseContentProvider = new DirectUrlWebContentProvider(false);

    String result = licenseContentProvider.getContentForUri("http://www.apache.org/licenses/LICENSE-2.0.txt")
        .getContent();
    LOG.debug(result);
    assertNotNull(result);
  }

}
