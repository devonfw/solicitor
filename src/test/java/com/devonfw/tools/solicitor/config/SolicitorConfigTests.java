/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SolicitorConfigTests {

    @Test
    public void contextLoads() {

    }

    @Test
    public void writeAndReadConfig() throws JsonGenerationException, JsonMappingException, IOException {

        SolicitorConfig solicitorConfig = new SolicitorConfig();
        solicitorConfig.setEngagementName("TestProject");
        solicitorConfig.setEngagementType(EngagementType.INTERN);
        solicitorConfig.setClientName("Legal Department");
        solicitorConfig.setGoToMarketModel(GoToMarketModel.LICENSE);
        solicitorConfig.setContractAllowsOss(true);
        solicitorConfig.setOssPolicyFollowed(true);
        solicitorConfig.setCustomerProvidesOss(false);

        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("testApplication");
        applicationConfig.setProgrammingEcosystem("Java8");
        applicationConfig.setReleaseId("0.0.1-RELEASE");
        applicationConfig.setSourceRepo("https://github.com/ohecker/solicitor.git");

        ReaderConfig readerConfig = new ReaderConfig();
        readerConfig.setType("maven");
        readerConfig.setSource("license.xml");

        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setType("dt");
        ruleConfig.setOptional(true);
        ruleConfig.setRuleSource("source_filename");
        ruleConfig.setTemplateSource("template filename");
        ruleConfig.setRuleGroup("theAgendaGroup");
        ruleConfig.setDescription("some decription");

        WriterConfig writerConfig = new WriterConfig();
        writerConfig.setType("xls");
        writerConfig.setTemplateSource("OSS-Inventory-Template-Test_20190414.xlsx");
        writerConfig.setTarget("OSS-Inventory_generated.xlsx");
        writerConfig.setDescription("The XLS OSS-Inventory document");
        Map<String, String> dataTables = new HashMap<>();
        dataTables.put("coll1", "test1");
        dataTables.put("coll2", "test2");
        writerConfig.setDataTables(dataTables);

        applicationConfig.getReaders().add(readerConfig);
        solicitorConfig.getApplications().add(applicationConfig);
        solicitorConfig.getRules().add(ruleConfig);
        solicitorConfig.getWriters().add(writerConfig);

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(new File("target/solicitor_gen.cfg"), solicitorConfig);

    }

}
