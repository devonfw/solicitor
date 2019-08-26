/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capgemini.solicitor.SolicitorSetup.ReaderSetup;
import com.capgemini.solicitor.common.DataTable;
import com.capgemini.solicitor.config.ConfigReader;
import com.capgemini.solicitor.config.WriterConfig;
import com.capgemini.solicitor.model.masterdata.Engagement;
import com.capgemini.solicitor.reader.Reader;
import com.capgemini.solicitor.reader.ReaderFactory;
import com.capgemini.solicitor.ruleengine.RuleEngine;
import com.capgemini.solicitor.writer.ResultDatabaseFactory;
import com.capgemini.solicitor.writer.Writer;
import com.capgemini.solicitor.writer.WriterFactory;

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