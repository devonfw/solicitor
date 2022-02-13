/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.ruleengine.drools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.devonfw.tools.solicitor.common.DeprecationChecker;

/**
 * Test methods of {@link ModelHelper}.
 */
public class ModelHelperTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.ruleengine.drools.ModelHelper#match(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testMatch() {

    // need to initialize on instance level but test is done on static methods
    ModelHelper instance = new ModelHelper();
    instance.setDeprecationChecker(new DeprecationChecker() {

      @Override
      public void check(boolean warnOnly, String detailsString) {

        // do nothing...
      }
    });
    assertTrue(ModelHelper.match(null, null));
    assertFalse(ModelHelper.match(null, ""));
    assertFalse(ModelHelper.match("", null));
    assertTrue(ModelHelper.match("", ""));
    assertTrue(ModelHelper.match("abc", "abc"));
    assertFalse(ModelHelper.match("abc", "a.c"));
    assertTrue(ModelHelper.match("abc", "REGEX:abc"));
    assertTrue(ModelHelper.match("abc", "REGEX:a.c"));
    assertFalse(ModelHelper.match("abc", "REGEX:a."));
    assertTrue(ModelHelper.match("abc", "REGEX:a.*"));
    assertTrue(ModelHelper.match("abc", "abc(REGEX)"));
    assertTrue(ModelHelper.match("abc", "a.c (REGEX)"));
    assertFalse(ModelHelper.match("abc", "a. (REGEX)"));
    assertTrue(ModelHelper.match("abc", "a.*(REGEX)"));

    assertTrue(ModelHelper.match("1.0", "RANGE:1.0"));
    assertTrue(ModelHelper.match("1.0.1", "RANGE:1.0"));
    assertFalse(ModelHelper.match("1.0.1", "RANGE:[1.0]"));
    assertTrue(ModelHelper.match("1.0.1", "RANGE:[1.0,1.1)"));
    assertFalse(ModelHelper.match("1.1", "RANGE:[1.0,1.1)"));

    assertTrue(ModelHelper.match(null, "NOT:"));
    assertFalse(ModelHelper.match("", "NOT:"));
    assertFalse(ModelHelper.match("abc", "NOT:abc"));
    assertFalse(ModelHelper.match("abc", "NOT:REGEX:abc"));
    assertTrue(ModelHelper.match("abc", "NOT:REGEX:a."));
    assertFalse(ModelHelper.match("abc", "NOT:abc(REGEX)"));
    assertTrue(ModelHelper.match("abc", "NOT:a. (REGEX)"));

    assertFalse(ModelHelper.match("1.0", "NOT:RANGE:1.0"));
    assertTrue(ModelHelper.match("1.0.1", "NOT:RANGE:[1.0]"));

  }

}
