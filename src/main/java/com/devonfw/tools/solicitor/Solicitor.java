/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;
import com.devonfw.tools.solicitor.SolicitorSetup.ReaderSetup;
import com.devonfw.tools.solicitor.common.LogMessages;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier.ResourceGroup;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.config.ConfigFactory;
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
    private ConfigFactory configFactory;

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

        resourceToFileCopier.copyReourcesToFile(ResourceGroup.USERGUIDE, ".");
    }

    /**
     * Create a new basic project configuration.
     * 
     * @param targetDir the directory where the userguide should be stored
     */
    private void wizard(String targetDir) {

        String location = resourceToFileCopier.copyReourcesToFile(ResourceGroup.PROJECT_FILES, targetDir);
        LOG.info(LogMessages.PROJECT_CREATED.msg(), location);
    }

    /**
     * Save all sample configuration files to the working directory.
     * 
     * @param targetDir the directory where the userguide should be stored
     */
    private void extractConfig(String targetDir) {

        String location = resourceToFileCopier.copyReourcesToFile(ResourceGroup.FULL_BASE_CONFIG, targetDir);
        LOG.info(LogMessages.FULL_CONFIG_EXTRACTED.msg(), location);
    }

    /**
     * Perform the main flow of processing.
     * 
     * @param clo the parsed command line options
     */
    private void mainProcessing(CommandLineOptions clo) {

        ModelRoot modelRoot = configFactory.createConfig(clo.configUrl);
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
        if (sv.isExtensionPresent()) {
            activateExtension(sv);
        }

        if (clo.extractUserGuide) {
            extractUserguide();
            doMainProcessing = false;
        }
        if (clo.extractConfig) {
            extractConfig(clo.targetDir);
            doMainProcessing = false;
        } else if (clo.wizard) {
            wizard(clo.targetDir);
            doMainProcessing = false;
        }
        if (doMainProcessing) {
            mainProcessing(clo);
        }
        LOG.info(LogMessages.COMPLETED.msg(), System.currentTimeMillis() - startTime);
    }

    /**
     * Checks the extension and logs info.
     * 
     * @param sv the object which holds info about the extension
     * @throws SolicitorRuntimeException if the extension is incompatible with
     *         this Solicitor version
     */
    private void activateExtension(SolicitorVersion sv) {

        LOG.info(LogMessages.EXTENSION_PRESENT.msg(), sv.getExtensionArtifact(), sv.getExtensionVersion(),
                sv.getExtensionGithash(), sv.getExtensionBuilddate());
        checkExtensionVersionRequirements(sv);
        if (sv.getExtensionMessage1() != null && !sv.getExtensionMessage1().isEmpty()) {
            LOG.info(sv.getExtensionMessage1());
        }
        if (sv.getExtensionMessage2() != null && !sv.getExtensionMessage2().isEmpty()) {
            LOG.info(sv.getExtensionMessage2());
        }
    }

    /**
     * Checks if Solicitor complies to the version expectations found in the
     * extension.
     * 
     * @param sv the object which holds info about the extension
     * @throws SolicitorRuntimeException if Solicitor does not comply to the
     *         version requirements given in the extension
     */
    private void checkExtensionVersionRequirements(SolicitorVersion sv) {

        if (sv.getExtensionExpectedSolicitorVersionRange() != null
                && !sv.getExtensionExpectedSolicitorVersionRange().isEmpty()) {
            DefaultArtifactVersion solicitorVersion = new DefaultArtifactVersion(sv.getVersion());
            VersionRange allowedRange;
            try {
                allowedRange = VersionRange.createFromVersionSpec(sv.getExtensionExpectedSolicitorVersionRange());
            } catch (InvalidVersionSpecificationException e) {
                throw new SolicitorRuntimeException(e);
            }
            if (!allowedRange.containsVersion(solicitorVersion)) {
                LOG.error(LogMessages.EXTENSION_EXPECTATION_FAILED.msg(),
                        sv.getExtensionExpectedSolicitorVersionRange());
                throw new SolicitorRuntimeException("Solicitor does not match version requirements of the extension");
            }
        }

    }

}
