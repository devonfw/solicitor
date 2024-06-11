package com.devonfw.tools.solicitor.componentinfo.curation.model;

import java.util.regex.Pattern;

import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * License finding curation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LicenseCuration {

  /**
   * Interface data structure for new license data (for ADD or REPLACE).
   */
  public static class NewLicenseData {
    /**
     * new license id
     */
    public String license;

    /**
     * new url pointing to license text
     */
    public String url;
  }

  private CurationOperation operation;

  private Pattern path;

  private Pattern ruleIdentifier;

  private Pattern matchedText;

  private Pattern oldLicense;

  private String newLicense;

  private String url;

  private String comment;

  /**
   * The constructor.
   */
  public LicenseCuration() {

  }

  /**
   * @return operation
   */
  public CurationOperation getOperation() {

    return this.operation;
  }

  /**
   * Returns the regular expressing which specifies the file path(s) to which this curation should apply.
   *
   * @return path
   */
  public String getPath() {

    return this.path.toString();
  }

  /**
   * Returns the regular expressing which specifies the rule identifiers(s) to which this curation should apply.
   *
   * @return ruleIdentifier
   */
  public String getRuleIdentifier() {

    return this.ruleIdentifier.toString();
  }

  /**
   * Returns the regular expressing which specifies the matched text(s) to which this curation should apply.
   *
   * @return matchedText
   */
  public String getMatchedText() {

    return this.matchedText.toString();
  }

  /**
   * Returns the regular expressing which specifies the old license (id) to which this curation should apply.
   *
   * @return new oldLicense
   */
  public String getOldLicense() {

    return this.oldLicense.toString();
  }

  /**
   * @return new newLicense
   */
  public String getNewLicense() {

    return this.newLicense;
  }

  /**
   * @return url
   */
  public String getUrl() {

    return this.url;
  }

  /**
   * Returns the
   *
   * @return comment
   */
  public String getComment() {

    return this.comment;
  }

  /**
   * @param operation new value of {@link #getOperation}.
   */
  public void setOperation(CurationOperation operation) {

    this.operation = operation;
  }

  /**
   * Sets the regular expression for specifying the file paths(s) to which this curation should apply.
   *
   * @param path new value of {@link #getPath}.
   */
  public void setPath(String path) {

    this.path = Pattern.compile(path);
  }

  /**
   * Sets the regular expression for specifying the rule identifier(s) to which this curation should apply
   *
   * @param ruleIdentifier new value of {@link #getRuleIdentifier}.
   */
  public void setRuleIdentifier(String ruleIdentifier) {

    this.ruleIdentifier = Pattern.compile(ruleIdentifier);
  }

  /**
   * Sets the regular expression for specifying the matched text(s) to which this curation should apply
   *
   * @param matchedText new value of {@link #getMatchedText}.
   */
  public void setMatchedText(String matchedText) {

    this.matchedText = Pattern.compile(matchedText, Pattern.DOTALL);
  }

  /**
   * Sets the regular expression for specifying the old license (id) to which this curation should apply
   *
   * @param oldLicense new value of {@link #getOldLicense}.
   */
  public void setOldLicense(String oldLicense) {

    this.oldLicense = Pattern.compile(oldLicense, Pattern.DOTALL);
  }

  /**
   * @param newLicense new value of {@link #getNewLicense}.
   */
  public void setNewLicense(String newLicense) {

    this.newLicense = newLicense;
  }

  /**
   * @param url new value of {@link #getUrl}.
   */
  public void setUrl(String url) {

    this.url = url;
  }

  /**
   * Sets the comment describing the curation
   *
   * @param comment new value of {@link #getComment}.
   */
  public void setComment(String comment) {

    this.comment = comment;
  }

  /**
   * Check if this is a REMOVE or REPLACE curation and matches the given data. If any of the conditions are not set (via
   * the appropriate setters or by reading the data from yaml) then the condition will be ignored.
   *
   * @param path the path to check
   * @param ruleIdentifier the ruleIdentifier to check
   * @param matchedText the matchedText to check
   * @param oldLicense the old license (as produced by the scancode rule) to check
   * @return <code>true</code> if this is a REMOVE or REPLACE curation and the conditions match, <code>false</code>
   *         otherwise.
   */
  @SuppressWarnings("hiding")
  public boolean matches(String path, String ruleIdentifier, String matchedText, String oldLicense) {

    if (this.operation != CurationOperation.REMOVE && this.operation != CurationOperation.REPLACE) {
      return false;
    }

    if (this.path != null && (path == null || !this.path.matcher(path).matches())) {
      return false;
    }
    if (this.ruleIdentifier != null
        && (ruleIdentifier == null || !this.ruleIdentifier.matcher(ruleIdentifier).matches())) {
      return false;
    }
    if (this.matchedText != null && (matchedText == null || !this.matchedText.matcher(matchedText).matches())) {
      return false;
    }
    if (this.oldLicense != null && (oldLicense == null || !this.oldLicense.matcher(oldLicense).matches())) {
      return false;
    }
    return true;

  }

  /**
   * Check if this is a ADD curation and matches the given path. If any of the conditions are not set (via the appropriate
   * setters or by reading the data from yaml) then the condition will be ignored.
   *
   * @param path the path to check
   * @return <code>true</code> if this is a ADD curation and the conditions match, <code>false</code> otherwise.
   */
  @SuppressWarnings("hiding")
  public boolean matches(String path) {

    if (this.operation != CurationOperation.ADD) {
      // no match if it is not an ADD curation
      return false;
    }

    if (this.path == null) {
      // if path condition is not set then this will only match if being called with <code>null</code> argument.
      return path == null;
    } else {
      if (path == null) {
        return false;
      }
      // if path condition is set the check if the given path matches the Pattern
      return this.path.matcher(path).matches();
    }

  }

  /**
   * Returns the new license data to be applied in case that the curation rule matches.
   *
   * @return the new license data. <code>null</code> indicates that the found license should be removed (REMOVE
   *         operation). If the data fields license or url contain <code>null</code> this indicates that they should
   *         remain unchanged. (Only applicable for REPLACE operation)
   */
  public NewLicenseData newLicenseData() {

    if (this.operation == CurationOperation.REMOVE) {
      // in case of REMOVE indicate this by returning null
      return null;
    }
    NewLicenseData result = new NewLicenseData();
    result.license = this.newLicense;
    result.url = this.url;
    return result;
  }

  /**
   * Validates if the data of this object is consistent.
   *
   * @throws CurationInvalidException if the object is invalid
   */
  public void validate() throws CurationInvalidException {

    if (this.operation == null) {
      throw new CurationInvalidException("Operation must not be null for license curation");
    }
    if (this.operation == CurationOperation.REMOVE || this.operation == CurationOperation.REPLACE) {
      if (this.path == null && this.ruleIdentifier == null && this.matchedText == null && this.oldLicense == null) {
        throw new CurationInvalidException(
            "For REMOVE/REPLACE license curation at least one condition must be defined");
      }
    }
    if (this.operation == CurationOperation.REMOVE) {
      if (this.newLicense != null || this.url != null) {
        throw new CurationInvalidException("For REMOVE license curation neither newLicense nor url must be set");
      }
    }
    if (this.operation == CurationOperation.REPLACE) {
      if (this.newLicense == null && this.url == null) {
        throw new CurationInvalidException("For REPLACE license curation at least license or url must be set");
      }
    }
    if (this.operation == CurationOperation.ADD) {
      if (this.ruleIdentifier != null || this.matchedText != null || this.oldLicense != null) {
        throw new CurationInvalidException(
            "For ADD license curation at neither ruleIdentifier nor matchedText nor oldLicense must be defined");
      }
      if (this.newLicense == null || this.url == null) {
        throw new CurationInvalidException("For ADD license curation license and url must be set");
      }
    }

  }

}