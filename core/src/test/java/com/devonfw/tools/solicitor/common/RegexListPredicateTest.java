package com.devonfw.tools.solicitor.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RegexListPredicate}.
 *
 */
class RegexListPredicateTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.common.RegexListPredicate#test(java.lang.String, java.lang.String)}.
   */
  @Test
  void testTestStringString() {

    RegexListPredicate objectUnderTest = new RegexListPredicate();
    assertFalse(objectUnderTest.test("", "Match! String: {}, Pattern: {}"));
    assertFalse(objectUnderTest.test(null, "Match! String: {}, Pattern: {}"));

    objectUnderTest.setRegexes(new String[] { ".*aa.*", "b.*" });

    assertFalse(objectUnderTest.test("", "Match! String: {}, Pattern: {}"));
    assertFalse(objectUnderTest.test(null, "Match! String: {}, Pattern: {}"));
    assertFalse(objectUnderTest.test("somelicense", "Match! String: {}, Pattern: {}"));
    assertFalse(objectUnderTest.test("abkkkkk", "Match! String: {}, Pattern: {}"));
    assertTrue(objectUnderTest.test("bkkkkk", "Match! String: {}, Pattern: {}"));
    assertTrue(objectUnderTest.test("aa", "Match! String: {}, Pattern: {}"));

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.common.RegexListPredicate#test(java.lang.String)}.
   */
  @Test
  void testTestString() {

    RegexListPredicate objectUnderTest = new RegexListPredicate();
    assertFalse(objectUnderTest.test(""));
    assertFalse(objectUnderTest.test(null));

    objectUnderTest.setRegexes(new String[] { ".*aa.*", "b.*" });

    assertFalse(objectUnderTest.test(""));
    assertFalse(objectUnderTest.test(null));
    assertFalse(objectUnderTest.test("somelicense"));
    assertFalse(objectUnderTest.test("abkkkkk"));
    assertTrue(objectUnderTest.test("bkkkkk"));
    assertTrue(objectUnderTest.test("aa"));
  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.common.RegexListPredicate#setRegexes(java.lang.String[])}.
   */
  @Test
  void testSetRegexes() {

    RegexListPredicate objectUnderTest = new RegexListPredicate();
    objectUnderTest.setRegexes(null);
    objectUnderTest.setRegexes(new String[] { ".*aa.*", "b.*" });
    Assertions.assertThrows(PatternSyntaxException.class, () -> {
      objectUnderTest.setRegexes(new String[] { "[ab" });
    });

  }

  /**
   * Test method for {@link com.devonfw.tools.solicitor.common.RegexListPredicate#getRegexesAsString()}.
   */
  @Test
  void testGetRegexesAsString() {

    RegexListPredicate objectUnderTest = new RegexListPredicate();
    assertEquals("", objectUnderTest.getRegexesAsString());

    objectUnderTest.setRegexes(new String[] { ".*aa.*", "b.*" });
    assertEquals(".*aa.*,b.*", objectUnderTest.getRegexesAsString());
  }

}
