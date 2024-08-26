package com.devonfw.tools.solicitor.reader.gradle;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GradleLicenseReportReaderTest {

    Application application;

    public GradleLicenseReportReaderTest() {
        ModelFactory modelFactory = new ModelFactoryImpl();
        this.application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
        GradleLicenseReportReader gr = new GradleLicenseReportReader();
        gr.setModelFactory(modelFactory);
        gr.setInputStreamFactory(new FileInputStreamFactory());
        gr.readInventory("gradle2", "src/test/resources/gradleLicenseReport.json", this.application, UsagePattern.STATIC_LINKING,
                         null, null, null);
    }

    @Test
    void testGetSupportedTypes() {
        GradleLicenseReportReader cut = new GradleLicenseReportReader();

        assertEquals(1, cut.getSupportedTypes().size());
        assertTrue(cut.getSupportedTypes().contains("gradle-license-report-json"));
    }

    @Test
    void testFindArtifact() {

        List<ApplicationComponent> lapc = this.application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getGroupId().equals("androidx.activity") && //
                    ap.getArtifactId().equals("activity") && //
                    ap.getVersion().equals("1.2.4")) {
                found = true;
                assertEquals("pkg:maven/androidx.activity/activity@1.2.4", ap.getPackageUrl());
                break;
            }
        }
        assertEquals(3, lapc.size());
        assertTrue(found);
    }

    @Test
    void testFindLicense() {

        List<ApplicationComponent> lapc = this.application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("istack-commons-runtime")
                    && ap.getRawLicenses().get(0).getDeclaredLicense().equals("GPL2 w/ CPE")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testMinimalInformation() {

        List<ApplicationComponent> lapc = this.application.getApplicationComponents();
        boolean found = false;
        for (ApplicationComponent ap : lapc) {
            if (ap.getArtifactId().equals("annotation")
                    && ap.getOssHomepage() == null
                    && ap.getRawLicenses().get(0).getDeclaredLicense() == null
                    && ap.getRawLicenses().get(0).getLicenseUrl() == null) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }
}