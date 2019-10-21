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
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Reads the JSON encoded application configuration.
 */
@Component
public class ConfigReader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);

    private static final int EXPECTED_CFG_VERSION = 1;

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

        checkConfigVersion(url);

        SolicitorConfig sc;
        try {
            sc = objectMapper.readValue(inputStreamFactory.createInputStreamFor(url), SolicitorConfig.class);
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not read config file", e);
        }
        return sc;
    }

    /**
     * Checks if the config file contains the correct version info.
     * 
     * @param url the location to read the config from
     * @throws SolicitorRuntimeException if the version info in the file does
     *         not exist or is not identical to the expected version
     *         {@value #EXPECTED_CFG_VERSION} or if the file could not be read.
     */
    private void checkConfigVersion(String url) {

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            JsonNode root = objectMapper.readTree(inputStreamFactory.createInputStreamFor(url));
            int version = 0;
            JsonNode versionNode = root.get("version");
            if (versionNode != null) {
                version = versionNode.asInt();
            }
            if (version != EXPECTED_CFG_VERSION) {
                LOG.error(LogMessages.UNSUPPORTED_CONFIG_VERSION.msg(), url, EXPECTED_CFG_VERSION, version);
                throw new SolicitorRuntimeException("Could not read config file due to version mismatch");
            }
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not read config file", e);
        }

    }

}
