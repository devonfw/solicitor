package com.devonfw.tools.solicitor.componentinfo.curation.model;

import java.util.regex.Pattern;

import com.devonfw.tools.solicitor.componentinfo.curation.CurationInvalidException;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Copyright curation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CopyrightCuration {

  private CurationOperation operation;

  private Pattern path;

  private Pattern oldCopyright;

  private String newCopyright;

  private String comment;

  /**
   * The constructor.
   */
  public CopyrightCuration() {

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
   * Returns the regular expressing which specifies the copyright(s) to which this curation should apply.
   *
   * @return copyright
   */
  public String getOldCopyright() {

    return this.oldCopyright.toString();
  }

  /**
   * @return newCopyright
   */
  public String getNewCopyright() {

    return this.newCopyright;
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
   * Sets the regular expression for specifying the copyright(s) to which this curation should apply.
   *
   * @param oldCopyright new value of {@link #getOldCopyright}.
   */
  public void setOldCopyright(String oldCopyright) {

    this.oldCopyright = Pattern.compile(oldCopyright, Pattern.DOTALL);
  }

  /**
   * @param newCopyright new value of {@link #getNewCopyright}.
   */
  public void setNewCopyright(String newCopyright) {

    this.newCopyright = newCopyright;
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
   * Check if this curation matches the given data. If any of the conditions are not set (via the appropriate setters or
   * by reading the data from yaml) then the condition will be ignored.
   *
   * @param path the path to check
   * @param copyright the ruleIdentifier to check
   * @return <code>true</code> if the conditions match, <code>false</code> otherwise.
   */
  @SuppressWarnings("hiding")
  public boolean matches(String path, String copyright) {

    if (this.operation != CurationOperation.REMOVE && this.operation != CurationOperation.REPLACE) {
      // this method should only match in case of REMOVE or REPLACE
      return false;
    }
    if (this.path != null && (path == null || !this.path.matcher(path).matches())) {
      return false;
    }
    if (this.oldCopyright != null && (copyright == null || !this.oldCopyright.matcher(copyright).matches())) {
      return false;
    }
    return true;

  }

  /**
   * Check if this a ADD curation and matches the given path. If any of the conditions are not set (via the appropriate
   * setters or by reading the data from yaml) then the condition will be ignored.
   *
   * @param path the path to check
   * @return <code>true</code> if this is an ADD curation and the conditions match, <code>false</code> otherwise.
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
      } else {
        // if path condition is set the check if the given path matches the Pattern
        return this.path.matcher(path).matches();
      }
    }

  }

  /**
   * Validates if the data of this object is consistent.
   *
   * @throws CurationInvalidException if the object is invalid
   */
  public void validate() throws CurationInvalidException {

    if (this.operation == null) {
      throw new CurationInvalidException("Operation must not be null for copyright curation");
    }
    if (this.operation == CurationOperation.REMOVE || this.operation == CurationOperation.REPLACE) {
      if (this.path == null && this.oldCopyright == null) {
        throw new CurationInvalidException(
            "For REMOVE/REPLACE copyright curation at least one condition must be defined");
      }
    }
    if (this.operation == CurationOperation.REMOVE) {
      if (this.newCopyright != null) {
        throw new CurationInvalidException("For REMOVE copyright curation the newCopyright must not be set");
      }
    }
    if (this.operation == CurationOperation.REPLACE) {
      if (this.newCopyright == null) {
        throw new CurationInvalidException("For REPLACE copyright curation newCopyright must be set");
      }
    }
    if (this.operation == CurationOperation.ADD) {
      if (this.oldCopyright != null) {
        throw new CurationInvalidException("For ADD copyright curation oldCopyright must not be defined");
      }
      if (this.newCopyright == null) {
        throw new CurationInvalidException("For ADD copyright curation newCopyright must be set");
      }
    }

  }

}
