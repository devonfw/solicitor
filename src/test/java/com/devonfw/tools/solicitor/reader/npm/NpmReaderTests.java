/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.npm;

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

public class NpmReaderTests {

    private static final Logger LOG = LoggerFactory.getLogger(NpmReaderTests.class);

    private Application application;

    public NpmReaderTests() {

        ModelFactory modelFactory = new ModelFactoryImpl();
        application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular");
        NpmReader nr = new NpmReader();
        nr.setModelFactory(modelFactory);
        nr.setInputStreamFactory(new FileInputStreamFactory());
        nr.readInventory("src/test/resources/npmlicenses.csv", application, UsagePattern.DYNAMIC_LINKING);
    }

    @Test
    public void readFile() {

        LOG.info(application.toString());
    }

    @Test
    public void testFindArtifact() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("to-fast-properties")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindDiffrentLicense() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("aws-sign2")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals("Apache-2.0")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindDiffrentLicense2() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("autoprefixer")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindDiffrentLicense3() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("yargs") && ap.getVersion().equals("9.0.1")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testFindLicense() {

        List<ApplicationComponent> lapc = application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("tough-cookie")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals("BSD-3-Clause")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}
