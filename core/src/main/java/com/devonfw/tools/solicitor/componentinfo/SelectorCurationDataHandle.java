package com.devonfw.tools.solicitor.componentinfo;

/**
 * An implementation of a {@link CurationDataHandle} which references curation data via a dataSelector. This
 * dataSelector is a string which might e.g. denote a branch in a Git repository. Further information (like e.g. the
 * PackageURL) is needed to retrieve the curation data.
 */
public class SelectorCurationDataHandle implements CurationDataHandle {

  private String curationDataSelector;

  /**
   * The constructor.
   *
   * @param curationDataSelector the curationDataSelector which is references the curation data. <code>null</code>
   *        indicates that the default should be used. <code>"none"</code> indicates that no curation should be applied.
   */
  public SelectorCurationDataHandle(String curationDataSelector) {

    this.curationDataSelector = curationDataSelector;

  }

  /**
   * Gets the curationDataSelctor encapsulated by this handle.
   *
   * @return the curationDataSelector
   */
  public String getCurationDataSelector() {

    return this.curationDataSelector;
  }

}
