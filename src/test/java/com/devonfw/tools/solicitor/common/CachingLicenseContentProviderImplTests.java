/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.devonfw.tools.solicitor.common.webcontent.HttpWebContentProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CachingLicenseContentProviderImplTests {

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
