/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Copies one or more resources to the file system.
 */
@Component
public class ResourceToFileCopier {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceToFileCopier.class);

    /**
     * Depicts different groups of resources which might be copied.
     */
    public static enum ResourceGroup {
        /**
         * The User Guide.
         */
        USERGUIDE,

        /**
         * The sample configuration file.
         */
        CONFIG_FILE_ONLY,

        /**
         * All configuration (configuration file, decision tables, templates)
         */
        CONFIG_FULL
    }

    @Autowired
    private InputStreamFactory inputStreamFactory;

    /**
     * Constructor.
     */
    public ResourceToFileCopier() {

    }

    /**
     * Copies all resources of the given group to the filesystem.
     * 
     * @param resourceGroup a {@link ResourceGroup} which identifies the files
     *        to copy.
     * @return some String possible used in subsequent logging - e.g. the name
     *         of the target resource
     */
    public String copyReourcesToFile(ResourceGroup resourceGroup) {

        String returnString;

        switch (resourceGroup) {
        case USERGUIDE:
            copyResourceToFile("classpath:solicitor_userguide.pdf", "solicitor_userguide.pdf");
            returnString = "solicitor_userguide.pdf";
            break;
        case CONFIG_FILE_ONLY:
            copyResourceToFile("classpath:samples/solicitor_sample.cfg", "solicitor_sample_copy.cfg");
            returnString = "solicitor_sample_copy.cfg";
            break;
        case CONFIG_FULL:
            copyResourceToFile("classpath:samples/solicitor_sample_filesystem.cfg", "solicitor_sample_copy.cfg");
            copyResourceToFile("classpath:samples/licenses_devon4j.xml", "licenses_devon4j.xml");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/rules/LicenseAssignmentSample.xls",
                    "sample_configs/LicenseAssignmentSample.xls");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/rules/LicenseNameMappingSample.xls",
                    "sample_configs/LicenseNameMappingSample.xls");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/rules/MultiLicenseSelectionSample.xls",
                    "sample_configs/MultiLicenseSelectionSample.xls");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/rules/LicenseSelectionSample.xls",
                    "sample_configs/LicenseSelectionSample.xls");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/rules/LegalPreEvaluationSample.xls",
                    "sample_configs/LegalPreEvaluationSample.xls");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/rules/LegalEvaluationSample.xls",
                    "sample_configs/LegalEvaluationSample.xls");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/templates/Solicitor_Output_Template_Sample.vm",
                    "sample_configs/Solicitor_Output_Template_Sample.vm");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/templates/Solicitor_Output_Template_Sample.xlsx",
                    "sample_configs/Solicitor_Output_Template_Sample.xlsx");
            copyResourceToFile("classpath:com/devonfw/tools/solicitor/templates/Solicitor_Diff_Template_Sample.vm",
                    "sample_configs/Solicitor_Diff_Template_Sample.vm");
            copyResourceToFile("classpath:samples/readme_solicitor_cfg.txt", "readme_solicitor_cfg.txt");
            returnString = "readme_solicitor_cfg.txt";
            break;
        default:
            throw new SolicitorRuntimeException("Uuups, this should never happen.");
        }

        return returnString;
    }

    /**
     * Copies a single reource to the file system.
     *
     * @param resourceUrl the URL of the resource to be used as source
     * @param fileName the name of the target file.
     */
    public void copyResourceToFile(String resourceUrl, String fileName) {

        File outputFile = new File(fileName);
        if (outputFile.exists()) {
            LOG.error(LogMessages.FILE_EXISTS.msg(), fileName);
            throw new SolicitorRuntimeException("Extracting of files aborted because target already exists");
        }
        LOG.info(LogMessages.COPYING_RESOURCE.msg(), resourceUrl, fileName);
        if (outputFile.getParentFile() != null) {
            // create needed directories if not yet existing
            outputFile.getParentFile().mkdirs();
        }
        try (InputStream inputStream = inputStreamFactory.createInputStreamFor(resourceUrl);
                OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not copy resource to file", e);
        }

    }

}
