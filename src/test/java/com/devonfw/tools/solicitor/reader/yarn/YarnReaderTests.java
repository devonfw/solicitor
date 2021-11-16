/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.yarn;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public class YarnReaderTests {
    private static final Logger LOG = LoggerFactory.getLogger(YarnReaderTests.class);

    Application application;

    public YarnReaderTests() {

        ModelFactory modelFactory = new ModelFactoryImpl();

        application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular");
        YarnReader gr = new YarnReader();
        gr.setModelFactory(modelFactory);
        gr.setInputStreamFactory(new FileInputStreamFactory());
        gr.readInventory("yarn", "src/test/resources/yarnReport.json", application,
                UsagePattern.DYNAMIC_LINKING, "yarn");

    }

    @Test
    public void findArtifact() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("test") && //
                    ap.getVersion().equals("11.0.0")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void readFile() {

        LOG.info(application.toString());
    }

    @Test
    public void testFindLicenseIfSingle() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("@test/testing") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testHomepageWhichIsGiven() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("test") && ap.getOssHomepage().equals("http://test.com")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testLicenseUrlWhichIsGiven() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("test")
                    && ap.getRawLicenses().get(0).getLicenseUrl().equals("https://github.com/mrtest/test")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

}
