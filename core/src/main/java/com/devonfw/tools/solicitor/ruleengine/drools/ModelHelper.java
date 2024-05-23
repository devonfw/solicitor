/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.ruleengine.drools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.DeprecationChecker;
import com.devonfw.tools.solicitor.common.MavenVersionHelper;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;

/**
 * Utility class for dealing with the data model. This is used/initialized as Spring bean, but static methods are also
 * used directly.
 */
@Component
public class ModelHelper {

  /**
   * Prefix which marks a string as defining a regular expression condition.
   */
  private static final String REGEX_PREFIX = "REGEX:";

  /**
   * Suffix which marks a string as defining a regular expression condition.
   */
  private static final String REGEX_SUFFIX = "(REGEX)";

  /**
   * Prefix which marks a string as defining a maven range condition.
   */
  private static final String RANGE_PREFIX = "RANGE:";

  /**
   * Prefix which marks a logical negation on the rest of the condition.
   */
  private static final String NOT_PREFIX = "NOT:";

  private static final Logger LOG = LoggerFactory.getLogger(ModelHelper.class);

  private static DeprecationChecker deprecationChecker;

  private static ModelFactory modelFactory;

  private static String currentRuleGroup;

  /**
   * Saves a reference to the {@link DeprecationChecker} in a static variable.
   *
   * @param deprecationChecker a {@link DeprecationChecker} object.
   */
  @Autowired
  public void setDeprecationChecker(DeprecationChecker deprecationChecker) {

    ModelHelper.deprecationChecker = deprecationChecker;
  }

  /**
   * Saves a reference to the ModelFactory in a static variable to allow access to factory methods via static methods.
   *
   * @param modelFactory a {@link ModelFactory} object.
   */
  @Autowired
  public void setModelFactory(ModelFactory modelFactory) {

    ModelHelper.modelFactory = modelFactory;
  }

  /**
   * Adds the given comment to the comments of the given license object.
   *
   * @param license a {@link NormalizedLicense} object.
   * @param comment the comment to append
   */
  public static void addCommentToNormalizedLicense(NormalizedLicense license, String comment) {

    String comments = license.getComments();

    if (comments == null) {
      comments = comment;
    } else {
      comments = comments + "; " + comment;
    }
    license.setComments(comments);
  }

  /**
   * Adds the given trace entry to the trace infos of the given license object.
   *
   * @param license a {@link NormalizedLicense} object.
   * @param traceEntry the trace entry to be added to the trace field
   */
  public static void appendTraceToNormalizedLicense(NormalizedLicense license, String traceEntry) {

    LOG.debug(traceEntry);
    String trace = license.getTrace();
    if (trace == null) {
      trace = traceEntry;
    } else {
      trace += System.lineSeparator() + traceEntry;
    }
    license.setTrace(trace);
  }

  /**
   * Creates a new {@link Application} by calling
   * {@link ModelFactory#newApplication(String, String, String, String, String)}.
   *
   * @param name the application name
   * @param releaseId the release id.
   * @param releaseDate the date of the release
   * @param sourceRepo pointer to the source repo
   * @param programmingEcosystem name of the programming ecosystem
   * @return the new instance
   */
  public static Application newApplication(String name, String releaseId, String releaseDate, String sourceRepo,
      String programmingEcosystem) {

    return modelFactory.newApplication(name, releaseId, releaseDate, sourceRepo, programmingEcosystem);
  }

  /**
   * Creates a new {@link ApplicationComponent} by calling {@link ModelFactory#newApplicationComponent()}.
   *
   * @return the new instance
   */
  public static ApplicationComponent newApplicationComponent() {

    return modelFactory.newApplicationComponent();
  }

  /**
   * Creates a new {@link Engagement} by calling
   * {@link ModelFactory#newEngagement(String, EngagementType, String, GoToMarketModel)} .
   *
   * @param engagementName the name of the engagement
   * @param engagementType the type of engagement
   * @param clientName name of the client
   * @param goToMarketModel the model how this goes to market
   * @return the new instance
   */
  public static Engagement newEngagement(String engagementName, EngagementType engagementType, String clientName,
      GoToMarketModel goToMarketModel) {

    return modelFactory.newEngagement(engagementName, engagementType, clientName, goToMarketModel);
  }

  /**
   * Creates a {@link NormalizedLicense} by calling {@link ModelFactory#newNormalizedLicense(RawLicense)}.
   *
   * @param rawLicense {@link RawLicense} from which to copy data
   * @return the new instance
   */
  public static NormalizedLicense newNormalizedLicense(RawLicense rawLicense) {

    return modelFactory.newNormalizedLicense(rawLicense);
  }

  /**
   * Creates a new {@link RawLicense} by calling {@link ModelFactory#newRawLicense()} on the implementation instance.
   *
   * @return the new instance
   */
  public static RawLicense newRawLicense() {

    return modelFactory.newRawLicense();
  }

  /**
   * Checks if the given first String matches the second String. By default this is done with simple string comparison.
   * There are some keywords which allow for advanced logic. If the seconds argument starts with "NOT:" then the rest of
   * the condition is inverted. The prefix "REGEX:" and the suffix "(REGEX)" indicate that the remainder should be
   * interpreted as a Java RegEx and matching will be done against this RegEx. "RANGE:" indicates that the following
   * should be interpreted as a Maven version range expression.
   *
   * @param input the string to test
   * @param condition the condition to test against
   * @return <code>true</code> if it matches, <code>false</code> otherwise
   */
  public static boolean match(String input, String condition) {

    if (input == null && condition == null) {
      return true;
    }
    if (condition != null) {
      if (condition.startsWith(NOT_PREFIX)) {
        String negatedCondition = condition.substring(NOT_PREFIX.length());
        return !match(input, negatedCondition);
      }
    }
    if (input != null && condition != null) {
      if (condition.startsWith(REGEX_PREFIX)) {
        deprecationChecker.check(false, "Use of 'REGEX:' prefix notation is deprecated, use '(REGEX)' suffix instead. "
            + "See https://github.com/devonfw/solicitor/issues/78 and https://github.com/devonfw/solicitor/issues/263");
        String pattern = condition.substring(REGEX_PREFIX.length());
        return input.matches(pattern);
      }
      if (condition.endsWith(REGEX_SUFFIX)) {
        String pattern = condition.substring(0, condition.length() - REGEX_SUFFIX.length()).trim();
        return input.matches(pattern);
      }
      if (condition.startsWith(RANGE_PREFIX)) {
        String rangeSpec = condition.substring(RANGE_PREFIX.length());
        return MavenVersionHelper.checkVersionRange(input, rangeSpec);
      }
      return input.equals(condition);
    }
    return false;
  }

  /**
   * This method gets the field <code>currentRuleGroup</code>. This field contains the id of the current rule group
   * being processed by the Drools Rule Engine.
   *
   * @return the field currentRuleGroup
   */
  public static String getCurrentRuleGroup() {

    return currentRuleGroup;
  }

  /**
   * This method sets the field <code>currentRuleGroup</code>.
   *
   * @param currentRuleGroup the new value of the field currentRuleGroup
   */
  public static void setCurrentRuleGroup(String currentRuleGroup) {

    ModelHelper.currentRuleGroup = currentRuleGroup;
  }

}
