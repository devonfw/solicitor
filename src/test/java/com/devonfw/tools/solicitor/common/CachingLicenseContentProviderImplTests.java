/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;

public class CachingLicenseContentProviderImplTests {

  private static final Logger LOG = LoggerFactory.getLogger(CachingLicenseContentProviderImplTests.class);

  @Test
  @Ignore
  public void readFile() {

    DirectUrlWebContentProvider licenseContentProvider = new DirectUrlWebContentProvider(false);

    String result = licenseContentProvider.getContentForUri("http://www.apache.org/licenses/LICENSE-2.0.txt")
        .getContent();
    LOG.debug(result);
    assertNotNull(result);
  }

}
