package com.devonfw.tools.solicitor.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ReportingGroupHandler}.
 */
class ReportingGroupHandlerTest {

  private ReportingGroupHandler handlerUnderTest;

  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {

    this.handlerUnderTest = new ReportingGroupHandler();
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#setReportingGroupActivationFilterPattern(java.lang.String)}.
   */
  @Test
  void testSetReportingGroupActivationFilterPattern() {

    this.handlerUnderTest.setReportingGroupActivationFilterPattern("some pattern");
    assertThrows(PatternSyntaxException.class,
        () -> this.handlerUnderTest.setReportingGroupActivationFilterPattern("[A-Z"));
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#validateReportingGroup(java.lang.String)}.
   */
  @Test
  void testValidateReportingGroup() {

    this.handlerUnderTest.validateReportingGroup("abcexyzABYZ_-09 ");
    // empty value
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroup(""));
    // disallowed characters
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroup("a#"));
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroup("aä"));
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroup("a("));
    // not starting alphanumeric
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroup("_a"));

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#validateReportingGroupList(java.lang.String)}.
   */
  @Test
  void testValidateReportingGroupList() {

    this.handlerUnderTest.validateReportingGroupList("#a b#c#");
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroupList("#a/b#c#"));
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroupList("a b#c#"));
    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroupList("#a b#c"));
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#splitReportingGroupList(java.lang.String)}.
   */
  @Test
  void testSplitReportingGroupList() {

    List<String> result = this.handlerUnderTest.splitReportingGroupList("#a b#c#");
    assertEquals(2, result.size());
    assertEquals("a b", result.get(0));
    assertEquals("c", result.get(1));

    assertThrows(SolicitorRuntimeException.class, () -> this.handlerUnderTest.validateReportingGroupList("#a/b#c#"));

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#normalizeReportingGroups(java.util.List)}.
   */
  @Test
  void testNormalizeReportingGroups() {

    Set<String> result;

    result = this.handlerUnderTest.normalizeReportingGroups(null);
    assertEquals(1, result.size());
    assertTrue(result.contains("default"));

    result = this.handlerUnderTest.normalizeReportingGroups(Arrays.asList(new String[] { "a", "b", "c", "b" }));
    assertEquals(3, result.size());
    assertTrue(result.containsAll(Arrays.asList(new String[] { "a", "b", "c" })));

    assertThrows(SolicitorRuntimeException.class,
        () -> this.handlerUnderTest.normalizeReportingGroups(Arrays.asList(new String[] { "/" })));
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#stringifyReportingGroups(java.util.Set)}.
   */
  @Test
  void testStringifyReportingGroups() {

    String result = this.handlerUnderTest
        .stringifyReportingGroups(new TreeSet<>(Arrays.asList(new String[] { "a", "b" })));

    assertTrue(result.equals("#a#b#") || result.equals("#b#a#"));
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#replacePlaceholderInSql(java.lang.String, java.lang.String)}.
   */
  @Test
  void testReplacePlaceholderInSql() {

    String result;
    result = this.handlerUnderTest.replacePlaceholderInSql("some sql #reportingGroup#with placeholder", "test");
    assertEquals("some sql #test#with placeholder", result);
    result = this.handlerUnderTest.replacePlaceholderInSql("some sql reportingGroup without placeholder", "test");
    assertEquals("some sql reportingGroup without placeholder", result);
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#expandReportingGroupInFileName(java.lang.String, java.lang.String)}.
   */
  @Test
  void testExpandReportingGroupInFileName() {

    String result;
    result = this.handlerUnderTest.expandReportingGroupInFileName(
        "some${/reportingGroup}/test${_reportingGroup}/file${_reportingGroup}${-reportingGroup}.txt", "foo");
    assertEquals("some/foo/test_foo/file_foo-foo.txt", result);

    result = this.handlerUnderTest.expandReportingGroupInFileName(
        "some${/reportingGroup}/test${_reportingGroup}/file${_reportingGroup}${-reportingGroup}.txt", "default");
    assertEquals("some/test/file.txt", result);
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#matchesReportingGroupFilter(java.lang.String)}.
   */
  @Test
  void testMatchesReportingGroupFilter() {

    assertTrue(this.handlerUnderTest.matchesReportingGroupFilter("some string"));
    this.handlerUnderTest.setReportingGroupActivationFilterPattern("foo|bar");
    assertTrue(this.handlerUnderTest.matchesReportingGroupFilter("foo"));
    assertTrue(this.handlerUnderTest.matchesReportingGroupFilter("bar"));
    assertFalse(this.handlerUnderTest.matchesReportingGroupFilter("test"));
    assertFalse(this.handlerUnderTest.matchesReportingGroupFilter("foo|bar"));

  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.ReportingGroupHandler#logReportingGroups(java.util.Collection)}.
   */
  @Test
  void testLogReportingGroups() {

    this.handlerUnderTest.setReportingGroupActivationFilterPattern("bar");
    this.handlerUnderTest.logReportingGroups(Arrays.asList(new String[] { "foo", "bar" }));
    // no assertion of actual log message; just assuring no exception is thrown
  }

}
