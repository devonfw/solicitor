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
import org.kie.internal.builder.conf.ParallelRulesBuildThresholdOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.InventoryProcessor;
import com.devonfw.tools.solicitor.SolicitorSetup;
import com.devonfw.tools.solicitor.common.DeprecationChecker;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.config.RuleConfig;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;

/**
 * Implementation of the {@link RuleEngine} interface using the <a href="https://www.drools.org/">Drools Rule
 * Engine</a>.
 *
 */
@Component
@Order(InventoryProcessor.RULE_ENGINE)
public class DroolsRuleEngine implements RuleEngine {

  static {
    // avoid parallel compiling of rules to avoid bug existing in Drools 8.17.0.Beta ... 8.31.0.Beta
    // see https://issues.redhat.com/browse/DROOLS-7210
    System.setProperty(ParallelRulesBuildThresholdOption.PROPERTY_NAME, "-1");
  }

  private static final Logger LOG = LoggerFactory.getLogger(DroolsRuleEngine.class);

  @Value("${drools-rule-engine.debuglog}")
  private String debugLog;

  @Autowired
  private DroolsRulesReaderFactory ruleReaderFactory;

  @Autowired
  private InputStreamFactory inputStreamFactory;

  @Autowired
  private SolicitorSetup setup;

  @Autowired
  private DeprecationChecker deprecationChecker;

  /**
   * {@inheritDoc}
   *
   * Each set of rules given by a RuleConfig (e.g. a single decision table) will be executed in a separate Kie session.
   */
  @Override
  public void processInventory(ModelRoot modelRoot) {

    int rulesFired = 0;
    for (RuleConfig rc : this.setup.getRuleSetups()) {
      rulesFired += executeRuleGroup(modelRoot, rc);
    }
    LOG.info(LogMessages.RULE_ENGINE_FINISHED.msg(), rulesFired);

  }

  /**
   * Execute the rules defined by a single RuleConfig. This includes building up the {@link KieSession}, adding all
   * rules, adding all facts (the model) and firing all rules. If the RuleConfig defines the rule group as optional and
   * the rule source does not exist the execution will be skipped.
   *
   * @param modelRoot the root to the model defining all facts
   * @param rc the configuration of the rules to execute
   * @return the number of rules which fired
   */
  private int executeRuleGroup(ModelRoot modelRoot, RuleConfig rc) {

    determineFinalRuleSourceName(rc);

    if (rc.isOptional() && !this.inputStreamFactory.isExisting(rc.getRuleSource())) {
      LOG.info(LogMessages.SKIPPING_RULEGROUP.msg(), rc.getRuleGroup(), rc.getRuleSource());
      return 0;
    }

    // check if this rule group was marked as deprecated
    if (rc.getDeprecationDetails() != null && !rc.getDeprecationDetails().isEmpty()) {
      this.deprecationChecker.check(rc.isDeprecationWarnOnly(), rc.getDeprecationDetails());
    }

    KieSession ksession = prepareSession(rc);

    insertFacts(ksession, modelRoot);

    // Fire the rules.
    long startTime = System.currentTimeMillis();
    ModelHelper.setCurrentRuleGroup(rc.getRuleGroup());
    int count = ksession.fireAllRules();
    long endTime = System.currentTimeMillis();
    LOG.info(LogMessages.RULE_GROUP_FINISHED.msg(), rc.getRuleGroup(), count, endTime - startTime);
    ksession.dispose();
    return count;
  }

  /**
   * Determine the final rule source name. If the resource given by {@link RuleConfig#getRuleSource()} exists then take
   * this. Otherwise check for alternatives by appending xls or csv suffix (with xls taking priority over csv).
   *
   * @param rc the configuration of the rule
   */
  private void determineFinalRuleSourceName(RuleConfig rc) {

    if (!this.inputStreamFactory.isExisting(rc.getRuleSource())) {
      if (this.inputStreamFactory.isExisting(rc.getRuleSource() + ".xls")) {
        if (this.inputStreamFactory.isExisting(rc.getRuleSource() + ".csv")) {
          LOG.warn(LogMessages.MULTIPLE_DECISIONTABLES.msg(), rc.getRuleSource());
        }
        rc.setRuleSource(rc.getRuleSource() + ".xls");
      } else if (this.inputStreamFactory.isExisting(rc.getRuleSource() + ".csv")) {
        rc.setRuleSource(rc.getRuleSource() + ".csv");
      }
    }
  }

  /**
   * Inserts all facts to the working memory of the given Drools session.
   *
   * @param ksession the Drools session
   * @param modelRoot root of the model containing the facts
   */
  private void insertFacts(KieSession ksession, ModelRoot modelRoot) {

    ksession.insert(modelRoot);
    Engagement engagement = modelRoot.getEngagement();
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
    LOG.info(LogMessages.ADDING_FACTS.msg(), ksession.getFactCount());
  }

  /**
   * Prepare the {@link KieSession} by reading and preprocessing given rules.
   *
   * @param rc the configuration of the rules to read
   * @return the prepared {@link KieSession}
   */
  private synchronized KieSession prepareSession(RuleConfig rc) {

    KieServices ks = KieServices.Factory.get();
    KieFileSystem kfs = ks.newKieFileSystem();

    ReleaseId rid = ks.newReleaseId("com.devonfw.tools", "solicitor", "0.0.0");
    kfs.generateAndWritePomXML(rid);

    KieModuleModel kModuleModel = ks.newKieModuleModel();
    KieBaseModel baseModel = kModuleModel.newKieBaseModel("SolicitorBaseModel");

    baseModel.addPackage("com.devonfw.tools.solicitor.rules");

    List<Resource> resources = new ArrayList<>();

    this.ruleReaderFactory.readerFor(rc.getType()).readRules(rc.getRuleSource(), rc.getTemplateSource(),
        rc.getDescription(), baseModel, resources);
    LOG.info(LogMessages.LOAD_RULES.msg(), rc.getType(), rc.getRuleSource(), rc.getTemplateSource(), rc.getRuleGroup());

    String sesionName = "SolitorSessionModel";
    baseModel.newKieSessionModel(sesionName);

    kfs.writeKModuleXML(kModuleModel.toXML());

    for (Resource r : resources) {
      kfs.write(r);
    }

    KieBuilder kb = ks.newKieBuilder(kfs);
    kb.buildAll();
    if (kb.getResults().hasMessages(Level.ERROR)) {
      throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
    }

    KieContainer kContainer = ks.newKieContainer(rid);

    KieSession kSession = kContainer.newKieSession(sesionName);

    if (this.debugLog != null && !this.debugLog.isEmpty()) {
      // Set up listeners.
      kSession.addEventListener(new DebugAgendaEventListener());
      kSession.addEventListener(new DebugRuleRuntimeEventListener());

      // Set up a file-based audit logger.
      KieServices.get().getLoggers().newFileLogger(kSession, this.debugLog);
    }

    return kSession;
  }
}
