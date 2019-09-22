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
import com.devonfw.tools.solicitor.common.DataTable;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier;
import com.devonfw.tools.solicitor.common.ResourceToFileCopier.ResourceGroup;
import com.devonfw.tools.solicitor.config.ConfigReader;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.ReaderFactory;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;
import com.devonfw.tools.solicitor.writer.ResultDatabaseFactory;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.WriterFactory;

@Component
public class Solicitor {

    private static final Logger LOG = LoggerFactory.getLogger(Solicitor.class);

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
    private ResourceToFileCopier resourceToFileCopier;

    @Autowired
    private ModelExporter modelExporter;

    public void run(CommandLineOptions clo) {

        boolean doMainProcessing = true;
        LOG.info("Solicitor starts");

        if (clo.externalizeUserguide) {
            externalizeUserguide();
            doMainProcessing = false;
        }
        if (doMainProcessing) {
            mainProcessing(clo);
        }
        LOG.info("Solicitor finished");
    }

    private void externalizeUserguide() {

        resourceToFileCopier.copyReourcesToFile(ResourceGroup.USERGUIDE);
    }

    private void mainProcessing(CommandLineOptions clo) {

        configReader.readConfig(clo.configUrl);
        readInventory();

        Engagement engagement = solicitorSetup.getEngagement();
        ruleEngine.executeRules(engagement);

        if (clo.save) {
            modelExporter.export(clo.pathForSave);
        }

        resultDatabaseFactory.initDataModel();

        writeResult();
    }

    private void readInventory() {

        for (ReaderSetup readerSetup : solicitorSetup.getReaderSetups()) {
            Reader reader = readerFactory.readerFor(readerSetup.getType());
            reader.readInventory(readerSetup.getSource(),
                    readerSetup.getApplication(),
                    readerSetup.getUsagePattern());
        }
    }

    private void writeResult() {

        for (WriterConfig writerConfig : solicitorSetup.getWriterSetups()) {
            Writer writer = writerFactory.writerFor(writerConfig.getType());
            writer.writeReport(writerConfig.getTemplateSource(),
                    writerConfig.getTarget(), getDataTables(writerConfig));
        }
    }

    private Map<String, DataTable> getDataTables(WriterConfig writerConfig) {

        Map<String, DataTable> result = new HashMap<>();
        for (Map.Entry<String, String> table : writerConfig.getDataTables()
                .entrySet()) {
            result.put(table.getKey(),
                    resultDatabaseFactory.getDataTable(table.getValue()));
        }
        return result;
    }

}