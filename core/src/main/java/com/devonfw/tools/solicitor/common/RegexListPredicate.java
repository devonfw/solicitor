/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates a list of regular expression {@link Pattern}s and allows checking if a string matches any of those
 * patterns.
 */
public class RegexListPredicate implements Predicate<String> {

  private static final Logger LOG = LoggerFactory.getLogger(RegexListPredicate.class);

  private Pattern[] patterns = new Pattern[0];

  /**
   * The constructor.
   */
  public RegexListPredicate() {

    super();
  }

  /**
   * Checks if the given argument matches any of the predefined regular expression patterns.
   *
   * @param t the string to be checked
   * @param debugLogTemplate template to be used for creating a debug log message if the argument matches. This template
   *        should have two placeholders <code>{}</code> which will be filled with the value of the argument and the
   *        matching pattern. If this parameter is set to <code>null</code> then no debug logging will be done.
   * @return <code>true</code> if the argument matches any of the patterns, <code>false</code> otherwise.
   */
  public boolean test(String t, String debugLogTemplate) {

    if (t == null) {
      return false;
    }
    for (Pattern p : this.patterns) {
      if (p.matcher(t).matches()) {
        if (LOG.isDebugEnabled() && debugLogTemplate != null) {
          LOG.debug(debugLogTemplate, t, p.toString());
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Tests the predicate without any debug logging.
   *
   * @param t the argument to be tested
   * @return <code>true</code> if the argument matches any of the patterns, <code>false</code> otherwise.
   * @see #test(String, String)
   * @see Predicate#test(Object)
   */
  @Override
  public boolean test(String t) {

    return test(t, null);
  }

  /**
   * Sets the regular expressions to be tested by this object.
   *
   * @param regexes Array of strings, each being a valid regular expression expression as defined in {@link Pattern}.
   * @throws PatternSyntaxException If any of the expressions have invalid syntax
   */
  public void setRegexes(String[] regexes) {

    if (regexes != null) {
      this.patterns = new Pattern[regexes.length];
      for (int i = 0; i < regexes.length; i++) {
        this.patterns[i] = Pattern.compile(regexes[i]);
      }
    }
  }

  /**
   * Returns the list of configured regular expressions as a String
   *
   * @return The regexes as a single string, separated via comma.
   */
  public String getRegexesAsString() {

    List<String> patternStrings = new ArrayList();
    for (Pattern p : this.patterns) {
      patternStrings.add(p.toString());
    }
    return String.join(",", patternStrings);
  }

}
