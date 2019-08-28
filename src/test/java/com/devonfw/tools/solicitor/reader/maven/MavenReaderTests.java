/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven;

import org.junit.Test;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.maven.MavenReader;

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
