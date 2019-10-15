/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Reads the JSON encoded application configuration.
 */
@Component
public class ConfigReader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);

    @Autowired
    private InputStreamFactory inputStreamFactory;

    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Reads a JSON configuration file.
     * 
     * @param url the location to read from
     * @return the read config
     */
    public SolicitorConfig readConfig(String url) {

        SolicitorConfig sc;
        try {
            sc = objectMapper.readValue(inputStreamFactory.createInputStreamFor(url), SolicitorConfig.class);
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not read config file", e);
        }
        return sc;
    }

}
