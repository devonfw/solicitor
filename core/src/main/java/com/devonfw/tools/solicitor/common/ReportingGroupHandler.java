/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Component which provides several methods for dealing with reporting groups.
 */
@Component
public class ReportingGroupHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ReportingGroupHandler.class);

  /**
   * Regular expression pattern string to check the validity of reporting group names. The name might only consist of
   * alphanumeric characters (US-ASCII), underscore, hyphen and space. It must begin with an alphanumeric character.
   * Other characters are forbidden to ensure list matching using {@value #REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER}
   * and to prevent SQL injection in the SQLs used in the reporting mechanism.
   */
  private static final String VALID_REPORTING_GROUP_REGEX = "[a-zA-Z0-9][a-zA-Z0-9_ -]*";

  /**
   * Java pattern representation of {@link #VALID_REPORTING_GROUP_REGEX}.
   */
  private static Pattern VALID_REPORTING_GROUP_PATTERN = Pattern.compile(VALID_REPORTING_GROUP_REGEX);

  /**
   * Name of the default reporting group to be used if no specific is given.
   */
  public static final String DEFAULT_REPORTING_GROUP_NAME = "default";

  /**
   * The delimiter / prefix / suffix to be used when storing a collection of reporting groups as a string.
   */
  public static final String REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER = "#";

  /**
   * Stringified list of reporting groups with only the default reporting group. To be used as default when reading
   * model data which does not contain information on the reporting group.
   */
  public static final String DEFAULT_REPORTING_GROUP_LIST = REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER
      + DEFAULT_REPORTING_GROUP_NAME + REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER;

  private static final String DEFAULT_REPORTING_GROUP_FILTER = ".*";

  private Pattern reportingGroupActivationFilterPattern = Pattern.compile(DEFAULT_REPORTING_GROUP_FILTER);

  /**
   * Sets the regex pattern which is used to determine if a specific reporting group is activated. Reporting groups are
   * activated if their name matches the given regular expression. Default is <code>.*</code> which matches any name.
   *
   * @param filterPattern the new value of the filter pattern
   */
  @Value("${solicitor.reportinggroups.filterpattern:.*}")
  public void setReportingGroupActivationFilterPattern(String filterPattern) {

    if (!DEFAULT_REPORTING_GROUP_FILTER.equals(filterPattern)) {
      LOG.info(LogMessages.REPORTING_GROUP_FILTER_EXPRESSION_SET_TO_NONDEFAULT.msg(), filterPattern);
    }

    this.reportingGroupActivationFilterPattern = Pattern.compile(filterPattern);
  }

  /**
   * Validates that the reporting group name consists only of permitted characters: Uppercase or lowercase, digits,
   * spaces, hyphens or underscores.
   *
   * @param reportingGroup the reporting group which should be validated
   * @throws SolicitorRuntimeException if the validation fails
   */
  public void validateReportingGroup(String reportingGroup) {

    if (!VALID_REPORTING_GROUP_PATTERN.matcher(reportingGroup).matches()) {
      LOG.error(LogMessages.ILLEGAL_CHARACTER_IN_REPORTING_GROUP.msg(), reportingGroup);
      throw new SolicitorRuntimeException("Illegal character in reportingGroup");
    }

  }

  /**
   * Splits a stringified list of reporting groups into a list. This includes validation.
   *
   * @param reportingGroups the stringified reporting group list which should be split
   * @return the list of reporting groups as strings
   * @throws SolicitorRuntimeException if the given argument is not a valid stringified list of reporting groups
   */
  public List<String> splitReportingGroupList(String reportingGroups) {

    if (!reportingGroups.startsWith("#")) {
      throw new SolicitorRuntimeException("Stringified reporting group list must start with #");
    }
    if (!reportingGroups.endsWith("#")) {
      throw new SolicitorRuntimeException("Stringified reporting group list must end with #");
    }
    String[] groups = reportingGroups.substring(1, reportingGroups.length() - 1)
        .split(REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER);
    for (String oneGroup : groups) {
      validateReportingGroup(oneGroup);
    }
    return Arrays.asList(groups);
  }

  /**
   * Validates a stringified list of reporting groups.
   *
   * @param reportingGroups the stringified reporting group list which should be validated
   * @throws SolicitorRuntimeException if the given argument is not a valid stringified list of reporting groups
   */
  public void validateReportingGroupList(String reportingGroups) {

    // just delegate
    splitReportingGroupList(reportingGroups);
  }

  /**
   * Normalizes and validates the list of reporting groups. In case that the list is null then a set containing the
   * default reporting groups will be returned. Otherwise the elements will be validated. Possible duplicates are
   * removed due to return datatype being a {@link Set}.
   *
   * @param reportingGroups the list of reporting groups to be normalized
   * @return the normalized set of reporting groups
   */
  public Set<String> normalizeReportingGroups(List<String> reportingGroups) {

    Set<String> normalizedReportingGroups = new TreeSet<>();
    if (reportingGroups == null) {
      normalizedReportingGroups.add(DEFAULT_REPORTING_GROUP_NAME);
    } else {
      for (String oneGroup : reportingGroups) {
        validateReportingGroup(oneGroup);
        normalizedReportingGroups.add(oneGroup);
      }
    }
    return normalizedReportingGroups;
  }

  /**
   * Converts the set of reporting groups to a single string via concatenation. All reporting group values are
   * surrounded with a # to enable easy finding / pattern matching.
   *
   * @param normalizedReportingGroups set of reporting groups
   * @return single string with all reporting groups
   */
  public String stringifyReportingGroups(Set<String> normalizedReportingGroups) {

    StringBuilder sb = new StringBuilder(REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER);
    for (String oneGroup : normalizedReportingGroups) {
      sb.append(oneGroup).append(REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER);
    }
    return sb.toString();
  }

  /**
   * Replaces the placeholder (if any) in the given sql string so that the actual reporting group value is used. SQL
   * injection is prevented because of previous validation of the possible <code>reportingGroup</code> values. (See
   * {@link ReportingGroupHandler#validateReportingGroup(String)} ).
   * <p>
   * The placeholder in the sql statement needs to be <code>"#reportingGroup#</code>. It will be replaced by the given
   * reporting group value surrounded by {@value ReportingGroupHandler#REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER}. <br>
   * So <code>a."reportingGroups" LIKE '%#reportingGroup#%' AND</code> <br>
   * will become <code>a."reportingGroups" LIKE '%#default#%' AND</code> if the current reporting group value is
   * <code>default</code>,
   *
   * @param sql the sql which possibly contains the placeholder(s) to be replaced.
   * @param reportingGroup the value of the current reporting group
   * @return the sql statement with replaced placeholder(s)
   */
  public String replacePlaceholderInSql(String sql, String reportingGroup) {

    return sql.replace("#reportingGroup#",
        REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER + reportingGroup + REPORTING_GROUP_STRINGIFIED_LIST_DELIMITER);
  }

  /**
   * Expands the reporting group name placeholders in the writer output filename (if any). Spaces which exist in the
   * reporting group name will be replaced by an underscore.
   * <p>
   * In case that the reporting group equals {@link #DEFAULT_REPORTING_GROUP_NAME} then the placeholders
   * <code>${reportingGroup}</code>, <code>${-reportingGroup}</code>, <code>${_reportingGroup}</code> and
   * <code>${/reportingGroup}</code> will be replaced by an empty string - thus removing the placeholder. Otherwise the
   * placeholders will be replaced with the reporting group name (prepending <code>-</code>, <code>_</code> or
   * <code>/</code> if applicable).
   *
   * @param rawFilename the raw filename with possible placeholder(s) <code>${reportingGroup}</code>.
   * @param reportingGroup the current reporting group
   * @return the filename with replaced placeholder(s)
   */
  public String expandReportingGroupInFileName(String rawFilename, String reportingGroup) {

    String targetFilename;
    if (DEFAULT_REPORTING_GROUP_NAME.equals(reportingGroup)) {
      targetFilename = rawFilename//
          .replace("${-reportingGroup}", "")//
          .replace("${_reportingGroup}", "")//
          .replace("${/reportingGroup}", "")//
          .replace("${reportingGroup}", "");
    } else {
      String reportingGroupUnderscored = reportingGroup.replace(" ", "_");
      targetFilename = rawFilename//
          .replace("${-reportingGroup}", "-" + reportingGroupUnderscored)//
          .replace("${_reportingGroup}", "_" + reportingGroupUnderscored)//
          .replace("${/reportingGroup}", "/" + reportingGroupUnderscored)//
          .replace("${reportingGroup}", reportingGroupUnderscored);
    }
    return targetFilename;
  }

  /**
   * Checks if the given reporting group matches the filter expression given by
   * {@link #reportingGroupActivationFilterPattern}.
   *
   * @param reportingGroup the reporting group name to check
   * @return <code>true</code> if the given argument matches the regular expression, <code>false</code> otherwise
   */
  public boolean matchesReportingGroupFilter(String reportingGroup) {

    return this.reportingGroupActivationFilterPattern.matcher(reportingGroup).matches();

  }

  /**
   * Logs the given reporting group names on INFO level. Any reporting groups which do not match the filter from
   * {@link #matchesReportingGroupFilter(String)} will be commented as disabled.
   *
   * @param reportingGroups the list of reporting groups
   */
  public void logReportingGroups(Collection<String> reportingGroups) {

    List<String> commentedReportingGroups = new ArrayList<>();
    for (String oneGroup : reportingGroups) {
      if (matchesReportingGroupFilter(oneGroup)) {
        commentedReportingGroups.add("'" + oneGroup + "'");
      } else {
        commentedReportingGroups.add("'" + oneGroup + "'" + " (disabled via filter)");
      }
    }
    LOG.info(LogMessages.REPORTING_GROUPS_DETECTED.msg(), String.join(", ", commentedReportingGroups));

  }
}
