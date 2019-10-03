/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.SolicitorSetup;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class ConfigReader {

    @Autowired
    private SolicitorSetup solicitorSetup;

    @Autowired
    private InputStreamFactory inputStreamFactory;

    @Autowired
    private ModelFactory modelFactory;

    ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public ModelRoot readConfig(String url) {

        SolicitorConfig sc;
        try {
            sc = objectMapper.readValue(
                    inputStreamFactory.createInputStreamFor(url),
                    SolicitorConfig.class);
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not read config file",
                    e);
        }

        ModelRoot modelRoot = modelFactory.newModelRoot();

        Engagement engagement = modelFactory.newEngagement(
                sc.getEngagementName(), sc.getEngagementType(),
                sc.getClientName(), sc.getGoToMarketModel());
        engagement.setModelRoot(modelRoot);
        engagement.setContractAllowsOss(sc.isContractAllowsOss());
        engagement.setOssPolicyFollowed(sc.isOssPolicyFollowed());
        engagement.setCustomerProvidesOss(sc.isCustomerProvidesOss());
        for (ApplicationConfig ac : sc.getApplications()) {
            Application app = modelFactory.newApplication(ac.getName(),
                    ac.getReleaseId(), "-UNDEFINED-", ac.getSourceRepo(),
                    ac.getProgrammingEcosystem());
            app.setEngagement(engagement);
            for (ReaderConfig rc : ac.getReaders()) {
                SolicitorSetup.ReaderSetup rs =
                        new SolicitorSetup.ReaderSetup();
                rs.setApplication(app);
                rs.setType(rc.getType());
                rs.setSource(rc.getSource());
                rs.setUsagePattern(rc.getUsagePattern());
                solicitorSetup.getReaderSetups().add(rs);
            }
        }
        solicitorSetup.setRuleSetups(sc.getRules());
        solicitorSetup.setWriterSetups(sc.getWriters());
        return modelRoot;
    }

}
