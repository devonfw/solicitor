/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;
import com.devonfw.tools.solicitor.SolicitorSetup.ReaderSetup;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier.ResourceGroup;
import com.devonfw.tools.solicitor.config.ConfigReader;
import com.devonfw.tools.solicitor.model.ModelImporterExporter;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.ReaderFactory;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;
import com.devonfw.tools.solicitor.writer.WriterFacade;

/**
 * The central bean of the Solicitor application.
 */
@Component
public class Solicitor {

    private static final Logger LOG = LoggerFactory.getLogger(Solicitor.class);

    @Autowired
    private SolicitorVersion solicitorVersion;

    @Autowired
    private SolicitorSetup solicitorSetup;

    @Autowired
    private ConfigReader configReader;

    @Autowired
    private ReaderFactory readerFactory;

    @Autowired
    private RuleEngine ruleEngine;

    @Autowired
    private WriterFacade writerFacade;

    @Autowired
    private ResourceToFileCopier resourceToFileCopier;

    @Autowired
    private ModelImporterExporter modelImporterExporter;

    /**
     * Copy the user guide to the current working directory.
     */
    private void extractUserguide() {

        resourceToFileCopier.copyReourcesToFile(ResourceGroup.USERGUIDE);
    }

    /**
     * Save a sample configuration file to the working directory.
     */
    private void extractConfig() {

        String location = resourceToFileCopier.copyReourcesToFile(ResourceGroup.CONFIG_FILE_ONLY);
        LOG.info(LogMessages.CONFIG_EXTRACTED.msg(), location);
    }

    /**
     * Save all sample configuration files to the working directory.
     */
    private void extractFullConfig() {

        String location = resourceToFileCopier.copyReourcesToFile(ResourceGroup.CONFIG_FULL);
        LOG.info(LogMessages.FULL_CONFIG_EXTRACTED.msg(), location);
    }

    /**
     * Perform the main flow of processing.
     * 
     * @param clo the parsed command line options
     */
    private void mainProcessing(CommandLineOptions clo) {

        ModelRoot modelRoot = configReader.readConfig(clo.configUrl);
        if (clo.load) {
            LOG.info(LogMessages.LOADING_DATAMODEL.msg(), clo.pathForLoad);
            modelRoot = modelImporterExporter.loadModel(clo.pathForLoad);
        } else {
            readInventory();
            ruleEngine.executeRules(modelRoot);
        }
        if (clo.save) {
            LOG.info(LogMessages.SAVING_DATAMODEL.msg(), clo.pathForSave);
            modelImporterExporter.saveModel(modelRoot, clo.pathForSave);
        }
        ModelRoot oldModelRoot = null;
        if (clo.diff) {
            LOG.info(LogMessages.LOADING_DIFF.msg(), clo.pathForDiff);
            oldModelRoot = modelImporterExporter.loadModel(clo.pathForDiff);
        }
        writerFacade.writeResult(modelRoot, oldModelRoot);
    }

    /**
     * Read the inventory of {@link ApplicationComponent}s and their declared
     * licenses.
     */
    private void readInventory() {

        for (ReaderSetup readerSetup : solicitorSetup.getReaderSetups()) {
            Reader reader = readerFactory.readerFor(readerSetup.getType());
            reader.readInventory(readerSetup.getSource(), readerSetup.getApplication(), readerSetup.getUsagePattern());
        }
    }

    /**
     * Starts processing as given by the command line options.
     *
     * @param clo the parsed command line options
     * @param rawCommandline the raw (unparsed) command line; for logging
     *        puposes only
     */
    public void run(CommandLineOptions clo, String rawCommandline) {

        LOG.info(LogMessages.CALLED.msg(), rawCommandline);

        long startTime = System.currentTimeMillis();
        boolean doMainProcessing = true;
        SolicitorVersion sv = solicitorVersion;
        LOG.info(LogMessages.STARTING.msg(), sv.getVersion(), sv.getGithash(), sv.getBuilddate());

        if (clo.extractUserGuide) {
            extractUserguide();
            doMainProcessing = false;
        }
        if (clo.extractFullConfig) {
            extractFullConfig();
            doMainProcessing = false;
        } else if (clo.extractConfig) {
            extractConfig();
            doMainProcessing = false;
        }
        if (doMainProcessing) {
            mainProcessing(clo);
        }
        LOG.info(LogMessages.COMPLETED.msg(), System.currentTimeMillis() - startTime);
    }

}
