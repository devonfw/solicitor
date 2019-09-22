/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Exports the data model.
 */
@Component
public class ModelExporter {
    private static final Logger LOG =
            LoggerFactory.getLogger(ModelExporter.class);

    private static class SolicitorState {
        @SuppressWarnings("unused")
        public String executionTime;

        @SuppressWarnings("unused")
        public Engagement engagement;
    }

    @Autowired
    private SolicitorSetup solicitorSetup;

    public void export(String filename) {

        SolicitorState state = new SolicitorState();
        state.executionTime = (new Date()).toString();
        state.engagement = solicitorSetup.getEngagement();

        String effectiveFilename = (filename != null) ? filename
                : "solicitor_" + System.currentTimeMillis() + ".json";
        ObjectMapper objectMapper =
                new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(effectiveFilename), state);
        } catch (IOException e) {
            LOG.error("Could not write internal data model to file '{}'",
                    effectiveFilename, e);
        }

    }
}
