/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

import jakarta.xml.bind.UnmarshalException;

public class MavenReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(MavenReaderTests.class);

  /**
   * Tests reading a maven license file.
   */
  @Test
  public void readFileAndCheckSize() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    MavenReader mr = new MavenReader();
    mr.setModelFactory(modelFactory);
    mr.setInputStreamFactory(new FileInputStreamFactory());
    mr.readInventory("maven", "src/test/resources/licenses_sample.xml", application, UsagePattern.DYNAMIC_LINKING, null,
        null);
    LOG.info(application.toString());
    assertEquals(95, application.getApplicationComponents().size());

    boolean found = false;
    for (ApplicationComponent ap : application.getApplicationComponents()) {
      if (ap.getGroupId().equals("org.springframework.boot") && //
          ap.getArtifactId().equals("spring-boot-starter-logging") && //
          ap.getVersion().equals("2.1.4.RELEASE")) {
        found = true;
        assertEquals("pkg:maven/org.springframework.boot/spring-boot-starter-logging@2.1.4.RELEASE",
            ap.getPackageUrl().toString());
        break;
      }
    }
    assertTrue(found);

  }

  /**
   * Tests reading a maven license file with enabled excludeFilter.
   */
  @Test
  public void readFileAndCheckSizeWithFilter() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    Map<String, String> configuration = new HashMap<>();
    configuration.put("excludeFilter", "pkg:maven/.*/log4j\\-to\\-slf4j@.*");
    MavenReader mr = new MavenReader();
    mr.setModelFactory(modelFactory);
    mr.setInputStreamFactory(new FileInputStreamFactory());
    mr.readInventory("maven", "src/test/resources/licenses_sample.xml", application, UsagePattern.DYNAMIC_LINKING, null,
        configuration);
    LOG.info(application.toString());
    assertEquals(94, application.getApplicationComponents().size());

  }

  /**
   * Tests if the MavenReader rejects XML with DOCTYPE declaration which is done to prevent XXE.
   */
  @Test
  public void testProtectionAgainstXxe() {

    ModelFactory modelFactory = new ModelFactoryImpl();

    Application application = modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com",
        "Java8", "#default#");
    MavenReader mr = new MavenReader();
    mr.setModelFactory(modelFactory);
    mr.setInputStreamFactory(new FileInputStreamFactory());

    try {
      mr.readInventory("maven", "src/test/resources/licenses_sample_with_doctype.xml", application,
          UsagePattern.DYNAMIC_LINKING, null, null);
      fail("Expected exception was not thrown");
    } catch (SolicitorRuntimeException e) {
      // we check detailed message to make sure the exception is not thrown due to other reasons
      UnmarshalException ume = (UnmarshalException) (e.getCause());
      assertTrue(ume.getLinkedException().getMessage().contains("DOCTYPE is disallowed"));
    }

  }
}
