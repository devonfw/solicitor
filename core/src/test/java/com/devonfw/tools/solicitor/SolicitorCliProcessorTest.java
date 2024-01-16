package com.devonfw.tools.solicitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;

/**
 * Tests for class {@link SolicitorCliProcessor}.
 *
 */
class SolicitorCliProcessorTest {

  @Test
  void testCliParsingHelpOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-eug" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.help);

    testCli = new String[] { "-h" };
    clo = scp.parse(testCli);
    assertTrue(clo.help);

    testCli = new String[] { "--help" };
    clo = scp.parse(testCli);
    assertTrue(clo.help);
  }

  @Test
  void testCliParsingEugOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.extractUserGuide);

    testCli = new String[] { "-eug" };
    clo = scp.parse(testCli);
    assertTrue(clo.extractUserGuide);

    testCli = new String[] { "--extractUserGuide" };
    clo = scp.parse(testCli);
    assertTrue(clo.extractUserGuide);
  }

  @Test
  void testCliParsingWizOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.wizard);

    testCli = new String[] { "-wiz", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.wizard);
    assertEquals("arg", clo.targetDir);

    testCli = new String[] { "--projectWizard", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.wizard);
    assertEquals("arg", clo.targetDir);
  }

  @Test
  void testCliParsingEcOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.extractConfig);

    testCli = new String[] { "-ec", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.extractConfig);
    assertEquals("arg", clo.targetDir);

    testCli = new String[] { "--extractConfig", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.extractConfig);
    assertEquals("arg", clo.targetDir);
  }

  @Test
  void testCliParsingConfigOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertNull(clo.configUrl);

    testCli = new String[] { "-c", "arg" };
    clo = scp.parse(testCli);
    assertEquals("arg", clo.configUrl);

    testCli = new String[] { "--config"/* , "arg" */ };
    // clo = scp.parse(testCli);
    // assertEquals("arg", clo.configUrl);
  }

  @Test
  void testCliParsingSaveModelOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.save);

    testCli = new String[] { "-s", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.save);
    assertEquals("arg", clo.pathForSave);

    testCli = new String[] { "--saveModel", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.save);
    assertEquals("arg", clo.pathForSave);

    testCli = new String[] { "-s" };
    clo = scp.parse(testCli);
    assertTrue(clo.save);
    assertNull(clo.pathForSave);

    testCli = new String[] { "--saveModel" };
    clo = scp.parse(testCli);
    assertTrue(clo.save);
    assertNull(clo.pathForSave);
  }

  @Test
  void testCliParsingLoadModelOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.load);

    testCli = new String[] { "-l", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.load);
    assertEquals("arg", clo.pathForLoad);

    testCli = new String[] { "--loadModel", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.load);
    assertEquals("arg", clo.pathForLoad);
  }

  @Test
  void testCliParsingDiffOption() {

    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    String[] testCli = new String[] { "-h" };
    CommandLineOptions clo = scp.parse(testCli);
    assertFalse(clo.diff);

    testCli = new String[] { "-d", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.diff);
    assertEquals("arg", clo.pathForDiff);

    testCli = new String[] { "--diff", "arg" };
    clo = scp.parse(testCli);
    assertTrue(clo.diff);
    assertEquals("arg", clo.pathForDiff);
  }
}
