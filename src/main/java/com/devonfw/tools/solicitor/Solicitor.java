/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorSetup.ReaderSetup;
import com.devonfw.tools.solicitor.common.DataTable;
import com.devonfw.tools.solicitor.config.ConfigReader;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.reader.ReaderFactory;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;
import com.devonfw.tools.solicitor.writer.ResultDatabaseFactory;
import com.devonfw.tools.solicitor.writer.Writer;
import com.devonfw.tools.solicitor.writer.WriterFactory;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Solicitor {

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

    public void run(String[] args) {

        LOG.info("Solicitor starts");
        configReader.readConfig(args[0]);
        readInventory();

        Engagement engagement = solicitorSetup.getEngagement();
        ruleEngine.executeRules(engagement);

        resultDatabaseFactory.setEngagement(engagement);

        writeResult();

        LOG.info("Solicitor finished");
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