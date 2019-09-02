/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;

/**
 */
@Component
public class ResourceToFileCopier {

    public static enum ResourceGroup {
        USERGUIDE
    }

    @Autowired
    private InputStreamFactory inputStreamFactory;

    /**
     * The Constructor. TODO ohecker
     */
    public ResourceToFileCopier() {

        // TODO Auto-generated constructor stub
    }

    public void copyReourcesToFile(ResourceGroup resourceGroup) {

        switch (resourceGroup) {
        case USERGUIDE:
            copyResourceToFile("classpath:solicitor_userguide.pdf",
                    "solicitor_userguide.pdf");
            break;
        }
    }

    public void copyResourceToFile(String resourceUrl, String fileName) {

        File outputFile = new File(fileName);
        try (InputStream inputStream =
                inputStreamFactory.createInputStreamFor(resourceUrl);
                OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not copy resource to file", e);
        }

    }

}
