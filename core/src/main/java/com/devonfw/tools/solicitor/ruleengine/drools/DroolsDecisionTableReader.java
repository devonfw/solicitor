/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine.drools;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;

/**
 * A {@link DroolsRulesReader} which reads decision tables in XLS format.
 */
@Component
public class DroolsDecisionTableReader implements DroolsRulesReader {

  @Autowired
  private InputStreamFactory inputStreamFactory;

  /**
   * Constructor.
   */
  public DroolsDecisionTableReader() {

  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(String type) {

    return "dt".equals(type);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("deprecation") // use of deprecated feature, see https://github.com/devonfw/solicitor/issues/245
  @Override
  public void readRules(String ruleSource, String templateSource, String decription, KieBaseModel baseModel,
      Collection<Resource> resources) {

    String ruleUuid = "com/devonfw/tools/solicitor/rules/" + UUID.randomUUID().toString();
    String templateUuid = "com/devonfw/tools/solicitor/rules/" + UUID.randomUUID().toString();

    baseModel.addRuleTemplate(ruleUuid, templateUuid, 2, 1);
    Resource dt;

    try {
      dt = ResourceFactory.newInputStreamResource(this.inputStreamFactory.createInputStreamFor(ruleSource));
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not open decision table resource '" + ruleSource + "' for reading");
    }
    dt.setSourcePath(ruleUuid);
    dt.setResourceType(ResourceType.DTABLE);

    DecisionTableConfigurationImpl dtcfg = new DecisionTableConfigurationImpl();
    if (ruleSource.endsWith("csv")) {
      dtcfg.setInputType(DecisionTableInputType.CSV);
    }
    dt.setConfiguration(dtcfg);
    resources.add(dt);
    try {
      dt = ResourceFactory.newInputStreamResource(this.inputStreamFactory.createInputStreamFor(templateSource));
    } catch (IOException e) {
      throw new SolicitorRuntimeException("Could not open rule template resource '" + templateSource + "'for reading");
    }
    dt.setSourcePath(templateUuid);
    dt.setResourceType(ResourceType.DRT);
    resources.add(dt);
  }

}
