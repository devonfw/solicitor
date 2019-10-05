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
        USERGUIDE
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
     */
    public void copyReourcesToFile(ResourceGroup resourceGroup) {

        switch (resourceGroup) {
        case USERGUIDE:
            copyResourceToFile("classpath:solicitor_userguide.pdf", "solicitor_userguide.pdf");
            break;
        }
    }

    /**
     * Copies a single reource to the file system.
     *
     * @param resourceUrl the URL of the resource to be used as source
     * @param fileName the name of the target file.
     */
    public void copyResourceToFile(String resourceUrl, String fileName) {

        File outputFile = new File(fileName);
        LOG.info(LogMessages.COPYING_RESOURCE.msg(), resourceUrl, fileName);
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
