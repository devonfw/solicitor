/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * Exports the data model.
 */
@Component
@Slf4j
public class ModelExporter {

    /**
     * The Constructor. TODO ohecker
     */
    public ModelExporter() {

        // TODO Auto-generated constructor stub
    }

    public void export(Engagement engagement, String filename) {

        String effectiveFilename = (filename != null) ? filename
                : "data" + System.currentTimeMillis() + ".json";
        ObjectMapper objectMapper =
                new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(effectiveFilename), engagement);
        } catch (IOException e) {
            LOG.error("Could not write internal data model to file '{}'",
                    effectiveFilename, e);
        }

    }
}
