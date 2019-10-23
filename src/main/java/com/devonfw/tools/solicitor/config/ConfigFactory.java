/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorSetup;
import com.devonfw.tools.solicitor.SolicitorSetup.ReaderSetup;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;

/**
 * Creates the Solicitor configuration by reading the project specific and the
 * base configuration and merging them.
 */
@Component
public class ConfigFactory {
    private static final String PROJECT_PLACEHOLDER = "${project}";

    private static final String CFG_DIR_PLACEHOLDER = "${cfgdir}";

    private static final String CONFIG_BASE = "base";

    private static final String CONFIG_PROJECT = "project";

    private static final Logger LOG = LoggerFactory.getLogger(ConfigFactory.class);

    @Autowired
    private ConfigReader configReader;

    @Autowired
    private SolicitorSetup solicitorSetup;

    @Autowired
    private ModelFactory modelFactory;

    @Value("${solicitor.base-config-url}")
    private String baseConfigUrl;

    /**
     * Gets the path contained in a file URL. If the argument is not a file URL
     * then a dot "." is returned.
     * <p>
     * Examples:
     * <ul>
     * <li><code>"file:foo.txt"</code> will return <code>"."</code></li>
     * <li><code>"classpath:some/path/foo.txt"</code> will return
     * <code>"."</code></li>
     * <li><code>"file:some/path/foo.txt"</code> will return
     * <code>"some/path"</code></li>
     * <li><code>"some/path/foo.txt"</code> will return <code>"."</code></li>
     * </ul>
     * 
     * @param url the URL to check
     * @return the path contained in the URL, excluding the trailing "/"
     */
    public static String getUrlPath(String url) {

        Pattern p = Pattern.compile("file:(.*)/");
        Matcher m = p.matcher(url);
        if (m.find()) {
            return m.group(1);
        } else {
            return ".";
        }
    }

    /**
     * Read the JSON configs (project specific and base config), merges them,
     * applies placeholder replacement and stores the result internally. Also
     * instantiates the masterdata model objects as defined in the configuration
     * and returns the {@link ModelRoot} object to allow further processing of
     * the model.
     *
     * @param url the URL which contains the JSON config
     * @return the created {@link ModelRoot} object.
     */
    public ModelRoot createConfig(String url) {

        LOG.info(LogMessages.READING_CONFIG.msg(), CONFIG_PROJECT, url);
        SolicitorConfig projectConfig = configReader.readConfig(url);
        LOG.info(LogMessages.READING_CONFIG.msg(), CONFIG_BASE, baseConfigUrl);
        SolicitorConfig baseConfig = configReader.readConfig(baseConfigUrl);

        SolicitorConfig sc = mergeConfigs(projectConfig, baseConfig);

        ModelRoot modelRoot = modelFactory.newModelRoot();

        Map<String, String> placeHolderMap = createPlaceholderMap(url, sc);

        LOG.info(LogMessages.CREATING_ENGAGEMENT.msg(), sc.getEngagementName());
        Engagement engagement = modelFactory.newEngagement(sc.getEngagementName(), sc.getEngagementType(),
                sc.getClientName(), sc.getGoToMarketModel());
        engagement.setModelRoot(modelRoot);
        engagement.setContractAllowsOss(sc.isContractAllowsOss());
        engagement.setOssPolicyFollowed(sc.isOssPolicyFollowed());
        engagement.setCustomerProvidesOss(sc.isCustomerProvidesOss());
        for (ApplicationConfig ac : sc.getApplications()) {
            LOG.info(LogMessages.CREATING_APPLICATION.msg(), ac.getName());
            Application app = modelFactory.newApplication(ac.getName(), ac.getReleaseId(), "-UNDEFINED-",
                    ac.getSourceRepo(), ac.getProgrammingEcosystem());
            app.setEngagement(engagement);
            for (ReaderConfig rc : ac.getReaders()) {
                SolicitorSetup.ReaderSetup rs = new SolicitorSetup.ReaderSetup();
                rs.setApplication(app);
                rs.setType(rc.getType());
                rs.setSource(rc.getSource());
                rs.setUsagePattern(rc.getUsagePattern());
                rs = resolvePlaceholdersInReader(rs, placeHolderMap);
                solicitorSetup.getReaderSetups().add(rs);
            }
        }
        solicitorSetup.setRuleSetups(resolvePlaceholdersInRules(sc.getRules(), placeHolderMap));
        solicitorSetup.setWriterSetups(resolvePlaceholdersInWriters(sc.getWriters(), placeHolderMap));
        return modelRoot;
    }

    /**
     * Creates the map of placeholders to resolve in the config.
     * 
     * @param configUrl the URL of the config
     * @param solicitorConfig the solicitor config
     * @return the map of placeholder patterns and their replacements.
     */
    private Map<String, String> createPlaceholderMap(String configUrl, SolicitorConfig solicitorConfig) {

        String projectSimpleName = solicitorConfig.getEngagementName().toLowerCase().replaceAll("\\W", "");
        Map<String, String> placeHolderMap = new TreeMap<>();
        placeHolderMap.put(PROJECT_PLACEHOLDER, projectSimpleName);
        placeHolderMap.put(CFG_DIR_PLACEHOLDER, getUrlPath(configUrl));
        for (Entry<String, String> placeHolder : placeHolderMap.entrySet()) {
            LOG.info(LogMessages.PLACEHOLDER_INFO.msg(), placeHolder.getKey(), placeHolder.getValue());
        }
        return placeHolderMap;
    }

    /**
     * Resolves the placeholder patterns in the reader setup (source file
     * location).
     * 
     * @param readerSetup the reader setup to operate on
     * @param placeHolderMap a map giving patterns and values of placeholders
     * @return the modified ReaderSetup
     */
    private ReaderSetup resolvePlaceholdersInReader(ReaderSetup readerSetup, Map<String, String> placeHolderMap) {

        readerSetup.setSource(resolvePlaceholders(readerSetup.getSource(), placeHolderMap));
        return readerSetup;
    }

    /**
     * Resolves the placeholders in the rule config (rule source and template
     * source).
     * 
     * @param rules the list of rule configs to operate on
     * @param placeHolderMap a map giving patterns and values of placeholders
     * @return the list of modified RuleConfigs
     */
    private List<RuleConfig> resolvePlaceholdersInRules(List<RuleConfig> rules, Map<String, String> placeHolderMap) {

        for (RuleConfig rc : rules) {
            rc.setRuleSource(resolvePlaceholders(rc.getRuleSource(), placeHolderMap));
            rc.setTemplateSource(resolvePlaceholders(rc.getTemplateSource(), placeHolderMap));
        }
        return rules;
    }

    /**
     * Resolves the placeholders in the writer configuration (target,
     * templateSource and location of SQL statemenents) string with the given
     * value.
     * 
     * @param writers the list of writers configs to operate on
     * @param placeHolderMap a map giving patterns and values of placeholders
     * @return the list of modified WriterConfigs
     */
    private List<WriterConfig> resolvePlaceholdersInWriters(List<WriterConfig> writers,
            Map<String, String> placeHolderMap) {

        for (WriterConfig wc : writers) {
            wc.setTarget(resolvePlaceholders(wc.getTarget(), placeHolderMap));
            wc.setTemplateSource(resolvePlaceholders(wc.getTemplateSource(), placeHolderMap));
            for (Entry<String, String> es : wc.getDataTables().entrySet()) {
                es.setValue(resolvePlaceholders(es.getValue(), placeHolderMap));
            }
        }
        return writers;
    }

    /**
     * Resolves the placeholders in the given string.
     * 
     * @param origString the original string
     * @param placeHolderMap a map giving patterns and values of placeholders
     * @return the modified string
     */
    private String resolvePlaceholders(String origString, Map<String, String> placeHolderMap) {

        String result = origString;
        for (Entry<String, String> placeholder : placeHolderMap.entrySet()) {
            result = result.replace(placeholder.getKey(), placeholder.getValue());
        }
        return result;
    }

    /**
     * Merges the two given configs. If the project config does not define rules
     * then the rule config from the base config will be taken. The same applies
     * for the writer config. All other config parameters will be taken from the
     * project config in any case.
     * 
     * @param projectConfig the project specific configuration
     * @param baseConfig the base configuration
     * @return the resulting/merged config
     */
    private SolicitorConfig mergeConfigs(SolicitorConfig projectConfig, SolicitorConfig baseConfig) {

        if (projectConfig.getRules() == null || projectConfig.getRules().size() == 0) {
            LOG.info(LogMessages.TAKING_RULE_CONFIG.msg(), CONFIG_BASE);
            projectConfig.setRules(baseConfig.getRules());
        } else {
            LOG.info(LogMessages.TAKING_RULE_CONFIG.msg(), CONFIG_PROJECT);
        }

        if (projectConfig.getWriters() == null || projectConfig.getWriters().size() == 0) {
            LOG.info(LogMessages.TAKING_WRITER_CONFIG.msg(), CONFIG_BASE);
            projectConfig.setWriters(baseConfig.getWriters());
        } else {
            LOG.info(LogMessages.TAKING_WRITER_CONFIG.msg(), CONFIG_PROJECT);
        }
        return projectConfig;
    }

    /**
     * Scans the base config for all classpath resources which are referenced.
     *
     * @return a collection of all classpath URLs found; if the base config
     *         itself is a classpath resource it will be included.
     */
    public List<String> findAllClasspathResourcesInBaseConfig() {

        // preserve order and avoid duplicates
        LinkedHashSet<String> result = new LinkedHashSet<>();

        result.add(baseConfigUrl);

        SolicitorConfig baseConfig = configReader.readConfig(baseConfigUrl);
        result.addAll(findResourcesInRules(baseConfig.getRules()));
        result.addAll(findResourcesInWriters(baseConfig.getWriters()));

        for (Iterator<String> i = result.iterator(); i.hasNext();) {
            if (!i.next().startsWith("classpath:")) {
                i.remove();
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * Returns all resource URLs configured for the rules.
     * 
     * @param rules the rules
     * @return collection of strings representing the URLs
     */
    private Collection<String> findResourcesInRules(List<RuleConfig> rules) {

        List<String> result = new ArrayList<>();
        for (RuleConfig rc : rules) {
            result.add(rc.getRuleSource());
            result.add(rc.getTemplateSource());
        }
        return result;
    }

    /**
     * Returns all resource URLs configured for the writers.
     * 
     * @param writers the rules
     * @return collection of strings representing the URLs
     */
    private Collection<String> findResourcesInWriters(List<WriterConfig> writers) {

        List<String> result = new ArrayList<>();
        for (WriterConfig wc : writers) {
            result.add(wc.getTemplateSource());
            result.addAll(wc.getDataTables().values());
        }
        return result;
    }

}
