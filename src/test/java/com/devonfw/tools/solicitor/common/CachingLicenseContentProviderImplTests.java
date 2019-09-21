/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.webcontent.HttpWebContentProvider;

public class CachingLicenseContentProviderImplTests {

    private static final Logger LOG = LoggerFactory
            .getLogger(CachingLicenseContentProviderImplTests.class);

    @Test
    public void readFile() {

        HttpWebContentProvider licenseContentProvider =
                new HttpWebContentProvider();

        String result = licenseContentProvider.getWebContentForUrl(
                "http://www.apache.org/licenses/LICENSE-2.0.txt");
        LOG.debug(result);
        assertNotNull(result);
    }

}
