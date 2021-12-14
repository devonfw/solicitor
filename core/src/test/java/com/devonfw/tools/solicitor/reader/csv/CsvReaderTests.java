/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import static org.junit.Assert.assertEquals;
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

public class CsvReaderTests {
    private static final Logger LOG = LoggerFactory.getLogger(CsvReaderTests.class);

    Application application;

    public CsvReaderTests() {

        ModelFactory modelFactory = new ModelFactoryImpl();

        this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
        CsvReader csvr = new CsvReader();
        csvr.setModelFactory(modelFactory);
        csvr.setInputStreamFactory(new FileInputStreamFactory());
        csvr.readInventory("csv", "src/test/resources/csvlicenses.csv", this.application, UsagePattern.DYNAMIC_LINKING,
                "maven","configuration");

    }

    @Test
    public void findArtifact() {

        List<ApplicationComponent> lapc = this.application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getGroupId().equals("org.antlr.runtime") && //
                    ap.getArtifactId().equals("antlr") && //
                    ap.getVersion().equals("4.6.0")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void readFileAndCheckSize() {

        LOG.info(this.application.toString());
        assertEquals(5, this.application.getApplicationComponents().size());
    }

    @Test
    public void testFindLicense() {

        List<ApplicationComponent> lapc = this.application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
        	System.out.println(ap.getRawLicenses().get(0).getLicenseUrl());
            if (ap.getArtifactId().equals("xtend") && ap.getRawLicenses().get(0).getDeclaredLicense().equals("MIT")
                    && ap.getRawLicenses().get(0).getLicenseUrl().equals("https://spdx.org/licenses/MIT#licenseText")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

}
