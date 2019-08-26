/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.reader.maven;

import org.junit.Test;

import com.capgemini.solicitor.common.FileInputStreamFactory;
import com.capgemini.solicitor.model.masterdata.Application;
import com.capgemini.solicitor.model.masterdata.UsagePattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenReaderTests {

    @Test
    public void readFile() {

        Application application = new Application("testApp", "0.0.0.TEST",
                "1.1.2111", "http://bla.com", "Java8");
        MavenReader mr = new MavenReader();
        mr.setInputStreamFactory(new FileInputStreamFactory());
        mr.readInventory("src/test/resources/licenses_sample.xml", application,
                UsagePattern.DYNAMIC_LINKING);
        LOG.info(application.toString());
    }

}
