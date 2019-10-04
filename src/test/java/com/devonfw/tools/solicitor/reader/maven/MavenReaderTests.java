/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public class MavenReaderTests {
    private static final Logger LOG = LoggerFactory.getLogger(MavenReaderTests.class);

    @Test
    public void readFile() {

        ModelFactory modelFactory = new ModelFactoryImpl();

        Application application =
                modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
        MavenReader mr = new MavenReader();
        mr.setModelFactory(modelFactory);
        mr.setInputStreamFactory(new FileInputStreamFactory());
        mr.readInventory("src/test/resources/licenses_sample.xml", application, UsagePattern.DYNAMIC_LINKING);
        LOG.info(application.toString());
    }

}
