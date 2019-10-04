/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;
import com.devonfw.tools.solicitor.SolicitorSetup.ReaderSetup;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier.ResourceGroup;
import com.devonfw.tools.solicitor.config.ConfigReader;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.model.ModelImporterExporter;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.ReaderFactory;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;
import com.devonfw.tools.solicitor.writer.ResultDatabaseFactory;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.WriterFactory;
import com.devonfw.tools.solicitor.writer.data.DataTable;
import com.devonfw.tools.solicitor.writer.data.DataTableDiffer;

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
    private ResultDatabaseFactory resultDatabaseFactory;

    @Autowired
    private WriterFactory writerFactory;

    @Autowired
    private DataTableDiffer dataTableDiffer;

    @Autowired
    private ResourceToFileCopier resourceToFileCopier;

    @Autowired
    private ModelImporterExporter modelImporterExporter;

    /**
     * Copy the user guide to the current working directory.
     */
    private void externalizeUserguide() {

        resourceToFileCopier.copyReourcesToFile(ResourceGroup.USERGUIDE);
    }

    /**
     * Execute the configured transformations via the embedded SQL database and
     * generated the data tables which will be input for the report generation
     * via XLS or velocity templating.
     * 
     * @param modelRoot the current model
     * @param oldModelRoot the old modelto compare to; might be
     *        <code>null</code>
     * @param writerConfig the configuration of a {@link Writer} which also
     *        defines the SQL queries to perform
     * @return a map of transformed data tables
     */
    private Map<String, DataTable> getDataTables(ModelRoot modelRoot, ModelRoot oldModelRoot,
            WriterConfig writerConfig) {

        // create the table for the current data model
        resultDatabaseFactory.initDataModel(modelRoot);
        Map<String, DataTable> result = new HashMap<>();
        for (Map.Entry<String, String> table : writerConfig.getDataTables().entrySet()) {
            result.put(table.getKey(), resultDatabaseFactory.getDataTable(table.getValue()));
        }
        // if old model data is defined then transform it and create diff
        // between new and old
        if (oldModelRoot != null) {
            resultDatabaseFactory.initDataModel(oldModelRoot);
            for (Map.Entry<String, String> table : writerConfig.getDataTables().entrySet()) {
                DataTable newTable = result.get(table.getKey());
                DataTable oldTable = resultDatabaseFactory.getDataTable(table.getValue());
                DataTable diffTable = dataTableDiffer.diff(newTable, oldTable);
                result.put(table.getKey(), diffTable);
            }
        }

        return result;
    }

    /**
     * Perform the main flow of processing.
     * 
     * @param clo the parsed command line options
     */
    private void mainProcessing(CommandLineOptions clo) {

        ModelRoot modelRoot = configReader.readConfig(clo.configUrl);
        if (clo.load) {
            modelRoot = modelImporterExporter.loadModel(clo.pathForLoad);
        } else {
            readInventory();
            ruleEngine.executeRules(modelRoot);
        }
        if (clo.save) {
            modelImporterExporter.saveModel(modelRoot, clo.pathForSave);
        }
        ModelRoot oldModelRoot = null;
        if (clo.diff) {
            oldModelRoot = modelImporterExporter.loadModel(clo.pathForDiff);
        }
        writeResult(modelRoot, oldModelRoot);
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
     */
    public void run(CommandLineOptions clo) {

        boolean doMainProcessing = true;
        LOG.info("Solicitor starts, Version:" + solicitorVersion.getVersion() + ", Buildnumber:"
                + solicitorVersion.getGithash() + ", Builddate:" + solicitorVersion.getBuilddate());

        if (clo.externalizeUserguide) {
            externalizeUserguide();
            doMainProcessing = false;
        }
        if (doMainProcessing) {
            mainProcessing(clo);
        }
        LOG.info("Solicitor finished");
    }

    /**
     * Write the result of processing to the configured reports.
     * 
     * @param modelRoot the model representing the result of processing
     * @param oldModelRoot an optional old model loaded from the filesystem to
     *        which the current result will be ompared to; might be
     *        <code>null</code>
     */
    private void writeResult(ModelRoot modelRoot, ModelRoot oldModelRoot) {

        for (WriterConfig writerConfig : solicitorSetup.getWriterSetups()) {
            Writer writer = writerFactory.writerFor(writerConfig.getType());
            writer.writeReport(writerConfig.getTemplateSource(), writerConfig.getTarget(),
                    getDataTables(modelRoot, oldModelRoot, writerConfig));
        }
    }

}
