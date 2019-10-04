/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine.drools;

import java.util.Collection;

import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.io.Resource;

/**
 * {@link DroolsRulesReader} read drools rules in a specific format from a given
 * resource.
 *
 */
public interface DroolsRulesReader {

    /**
     * Indicate whether the given instance of the {@link DroolsRulesReader} is
     * appropriate for reading the specified rule format.
     * 
     * @param type the type of rules to read
     * @return <code>true</code> if the format is accepted, <code>false</code>
     *         otherwise
     */
    public boolean accept(String type);

    /**
     * Reads the rules.
     *
     * @param ruleSource a URL of the resource which contains the rules (data)
     * @param templateSource a URL of a resource which contains possible rule
     *        templates to use
     * @param decription a description of the rules
     * @param baseModel the {@link KieBaseModel} to be used
     * @param resources the collection of {@link Resource} to which the read
     *        items should be added
     */
    public void readRules(String ruleSource, String templateSource, String decription, KieBaseModel baseModel,
            Collection<Resource> resources);

}
