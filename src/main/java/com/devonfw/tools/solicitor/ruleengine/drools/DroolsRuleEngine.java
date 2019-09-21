/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine.drools;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorSetup;
import com.devonfw.tools.solicitor.config.RuleConfig;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;
import com.google.common.collect.Lists;

@Component
public class DroolsRuleEngine implements RuleEngine {

    private static final Logger LOG =
            LoggerFactory.getLogger(DroolsRuleEngine.class);

    @Value("${drools-rule-engine.debuglog}")
    private String debugLog;

    @Autowired
    private DroolsRulesReaderFactory ruleReaderFactory;

    @Autowired
    private SolicitorSetup setup;

    @Override
    public void executeRules(Engagement engagement) {

        KieSession ksession = prepareSession();

        if (debugLog != null && !debugLog.isEmpty()) {
            // Set up listeners.
            ksession.addEventListener(new DebugAgendaEventListener());
            ksession.addEventListener(new DebugRuleRuntimeEventListener());

            // Set up a file-based audit logger.
            KieServices.get().getLoggers().newFileLogger(ksession, debugLog);
        }

        ksession.insert(engagement);
        for (Application app : engagement.getApplications()) {
            ksession.insert(app);
            for (ApplicationComponent ac : app.getApplicationComponents()) {
                ksession.insert(ac);
                for (NormalizedLicense lic : ac.getNormalizedLicenses()) {
                    ksession.insert(lic);
                }
                for (RawLicense lic : ac.getRawLicenses()) {
                    ksession.insert(lic);
                }
            }
        }

        // Fire the rules.
        int count = ksession.fireAllRules();
        LOG.info("Number of Rules Fired: " + count);
        ksession.dispose();
    }

    private KieSession prepareSession() {

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        ReleaseId rid =
                ks.newReleaseId("com.devonfw.tools", "solicitor", "0.0.1");
        kfs.generateAndWritePomXML(rid);

        KieModuleModel kModuleModel = ks.newKieModuleModel();
        KieBaseModel baseModel =
                kModuleModel.newKieBaseModel("ProgrammaticTestBaseModel");

        baseModel.addPackage("com.devonfw.tools.solicitor.rules");

        List<Resource> resources = new ArrayList<>();
        List<String> agendaGroups = new ArrayList<>();

        for (RuleConfig rs : setup.getRuleSetups()) {
            ruleReaderFactory.readerFor(rs.getType()).readRules(
                    rs.getRuleSource(), rs.getTemplateSource(),
                    rs.getDescription(), baseModel, resources);
            agendaGroups.add(rs.getAgendaGroup());
        }

        baseModel.newKieSessionModel("LicenseNameMappingKSProgrammatic");

        kfs.writeKModuleXML(kModuleModel.toXML());

        for (Resource r : resources) {
            kfs.write(r);
        }

        KieBuilder kb = ks.newKieBuilder(kfs);
        kb.buildAll();
        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException(
                    "Build Errors:\n" + kb.getResults().toString());
        }

        KieContainer kContainer = ks.newKieContainer(rid);

        KieSession kSession =
                kContainer.newKieSession("LicenseNameMappingKSProgrammatic");
        for (String ag : Lists.reverse(agendaGroups)) {
            kSession.getAgenda().getAgendaGroup(ag).setFocus();
        }

        return kSession;
    }
}
