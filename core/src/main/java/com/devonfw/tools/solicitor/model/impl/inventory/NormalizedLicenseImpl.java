/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.model.impl.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LicenseTextHelper;
import com.devonfw.tools.solicitor.common.content.ContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;
import com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContent;
import com.devonfw.tools.solicitor.model.impl.AbstractModelObject;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of the {@link NormalizedLicense} model object interface.
 */
public class NormalizedLicenseImpl extends AbstractModelObject implements NormalizedLicense {

  private static final Logger LOG = LoggerFactory.getLogger(NormalizedLicenseImpl.class);

  private String declaredLicense;

  private String licenseUrl;

  private String declaredLicenseContentKey;

  private String normalizedLicenseType;

  private String normalizedLicense;

  private String normalizedLicenseUrl;

  private String normalizedLicenseContentKey;

  private String effectiveNormalizedLicenseType;

  private String effectiveNormalizedLicense;

  private String effectiveNormalizedLicenseUrl; // really needed?

  private String effectiveNormalizedLicenseContentKey;

  private String legalPreApproved;

  private String copyLeft;

  private String licenseCompliance;

  private String licenseRefUrl;

  private String licenseRefContentKey;

  private String includeLicense;

  private String includeSource;

  private String reviewedForRelease;

  private String comments;

  private String legalApproved;

  private String legalComments;

  private String trace;

  private String guessedLicenseUrl;

  private String guessedLicenseContentKey;

  private String guessedLicenseUrlAuditInfo;

  private ApplicationComponent applicationComponent;

  private ContentProvider<WebContent> licenseContentProvider;

  private ContentProvider<GuessedLicenseUrlContent> licenseUrlGuesser;

  /**
   * Creates a new instance.
   */
  public NormalizedLicenseImpl() {

  }

  /**
   * Creates a a new instance.
   *
   * @param rawLicense the raw license which this object should be based on; identical fields will be copied.
   */
  public NormalizedLicenseImpl(RawLicense rawLicense) {

    setApplicationComponent(rawLicense.getApplicationComponent());
    this.declaredLicense = rawLicense.getDeclaredLicense();
    this.licenseUrl = rawLicense.getLicenseUrl();
    setDeclaredLicenseContent(rawLicense.getDeclaredLicenseContent());
    this.trace = rawLicense.getTrace();
  }

  /** {@inheritDoc} */
  @Override
  protected ApplicationComponent doGetParent() {

    return this.applicationComponent;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public ApplicationComponent getApplicationComponent() {

    return this.applicationComponent;
  }

  /** {@inheritDoc} */
  @Override
  public String getComments() {

    return this.comments;
  }

  /** {@inheritDoc} */
  @Override
  public String getCopyLeft() {

    return this.copyLeft;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getDataElements() {

    return new String[] { this.declaredLicense, this.licenseUrl, getDeclaredLicenseContent(),
    this.normalizedLicenseType, this.normalizedLicense, this.normalizedLicenseUrl, getNormalizedLicenseContent(),
    this.effectiveNormalizedLicenseType, this.effectiveNormalizedLicense, this.effectiveNormalizedLicenseUrl,
    getEffectiveNormalizedLicenseContent(), this.legalPreApproved, this.copyLeft, this.licenseCompliance,
    this.licenseRefUrl, getLicenseRefContent(), this.includeLicense, this.includeSource, this.reviewedForRelease,
    this.comments, this.legalApproved, this.legalComments, this.trace, this.guessedLicenseUrl,
    this.guessedLicenseUrlAuditInfo, getGuessedLicenseContent() };
  }

  /** {@inheritDoc} */
  @Override
  public String getDeclaredLicense() {

    return this.declaredLicense;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public String getDeclaredLicenseContent() {

    return LicenseTextHelper.replaceLongHtmlContent(retrieveTextFromPool(this.declaredLicenseContentKey));
  }

  /**
   * Gets the text pool key of the {@link #getDeclaredLicenseContent()}.
   *
   * @return the key
   */
  public String getDeclaredLicenseContentKey() {

    return this.declaredLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public String getEffectiveNormalizedLicense() {

    return this.effectiveNormalizedLicense;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public String getEffectiveNormalizedLicenseContent() {

    return LicenseTextHelper.replaceLongHtmlContent(retrieveTextFromPool(this.effectiveNormalizedLicenseContentKey));
  }

  /**
   * Gets the text pool key of the {@link #getEffectiveNormalizedLicenseContent()}.
   *
   * @return the key
   */
  public String getEffectiveNormalizedLicenseContentKey() {

    return this.effectiveNormalizedLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public String getEffectiveNormalizedLicenseType() {

    return this.effectiveNormalizedLicenseType;
  }

  /** {@inheritDoc} */
  @Override
  public String getEffectiveNormalizedLicenseUrl() {

    return this.effectiveNormalizedLicenseUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String[] getHeadElements() {

    return new String[] { "declaredLicense", "licenseUrl", "declaredLicenseContent", "normalizedLicenseType",
    "normalizedLicense", "normalizedLicenseUrl", "normalizedLicenseContent", "effectiveNormalizedLicenseType",
    "effectiveNormalizedLicense", "effectiveNormalizedLicenseUrl", "effectiveNormalizedLicenseContent",
    "legalPreApproved", "copyLeft", "licenseCompliance", "licenseRefUrl", "licenseRefContent", "includeLicense",
    "includeSource", "reviewedForRelease", "comments", "legalApproved", "legalComments", "trace", "guessedLicenseUrl",
    "guessedLicenseUrlAuditInfo", "guessedLicenseContent" };
  }

  /** {@inheritDoc} */
  @Override
  public String getIncludeLicense() {

    return this.includeLicense;
  }

  /** {@inheritDoc} */
  @Override
  public String getIncludeSource() {

    return this.includeSource;
  }

  /** {@inheritDoc} */
  @Override
  public String getLegalApproved() {

    return this.legalApproved;
  }

  /** {@inheritDoc} */
  @Override
  public String getLegalComments() {

    return this.legalComments;
  }

  /** {@inheritDoc} */
  @Override
  public String getLegalPreApproved() {

    return this.legalPreApproved;
  }

  /** {@inheritDoc} */
  @Override
  public String getLicenseCompliance() {

    return this.licenseCompliance;
  }

  /**
   * This method gets the field <code>licenseContentProvider</code>.
   *
   * @return the field licenseContentProvider
   */
  @JsonIgnore
  public ContentProvider<WebContent> getLicenseContentProvider() {

    return this.licenseContentProvider;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public String getLicenseRefContent() {

    return LicenseTextHelper.replaceLongHtmlContent(retrieveTextFromPool(this.licenseRefContentKey));
  }

  /**
   * Gets the text pool key of the {@link #getLicenseRefContent()}.
   *
   * @return the key
   */
  public String getLicenseRefContentKey() {

    return this.licenseRefContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public String getLicenseRefUrl() {

    return this.licenseRefUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getLicenseUrl() {

    return this.licenseUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getNormalizedLicense() {

    return this.normalizedLicense;
  }

  /** {@inheritDoc} */
  @Override
  public String getNormalizedLicenseType() {

    return this.normalizedLicenseType;
  }

  /** {@inheritDoc} */
  @Override
  public String getNormalizedLicenseUrl() {

    return this.normalizedLicenseUrl;
  }

  /** {@inheritDoc} */
  @Override
  @JsonIgnore
  public String getNormalizedLicenseContent() {

    return LicenseTextHelper.replaceLongHtmlContent(retrieveTextFromPool(this.normalizedLicenseContentKey));
  }

  /**
   * Gets the text pool key of the {@link #getNormalizedLicenseContent()}.
   *
   * @return the key
   */
  public String getNormalizedLicenseContentKey() {

    return this.normalizedLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public String getReviewedForRelease() {

    return this.reviewedForRelease;
  }

  /** {@inheritDoc} */
  @Override
  public String getTrace() {

    return this.trace;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getGuessedLicenseUrl() {

    return this.guessedLicenseUrl;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getGuessedLicenseUrlAuditInfo() {

    return this.guessedLicenseUrlAuditInfo;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  @JsonIgnore
  public String getGuessedLicenseContent() {

    return LicenseTextHelper.replaceLongHtmlContent(retrieveTextFromPool(this.guessedLicenseContentKey));
  }

  /**
   * Gets the text pool key of the {@link #getGuessedLicenseContent()}.
   *
   * @return the key
   */
  public String getGuessedLicenseContentKey() {

    return this.guessedLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public void setApplicationComponent(ApplicationComponent applicationComponent) {

    if (this.applicationComponent != null) {
      throw new IllegalStateException("Once the ApplicationComponentImpl is set it can not be changed");
    }
    this.applicationComponent = applicationComponent;
    applicationComponent.addNormalizedLicense(this);
  }

  /** {@inheritDoc} */
  @Override
  public void setComments(String comments) {

    this.comments = comments;
  }

  /** {@inheritDoc} */
  @Override
  public void setCopyLeft(String copyLeft) {

    this.copyLeft = copyLeft;
  }

  /** {@inheritDoc} */
  @Override
  public void setDeclaredLicense(String declaredLicense) {

    this.declaredLicense = declaredLicense;
  }

  /**
   * Sets the declaredLicenseContent.
   *
   * @param declaredLicenseContent the content to set
   */
  @Override
  public void setDeclaredLicenseContent(String declaredLicenseContent) {

    this.declaredLicenseContentKey = storeTextInPool(declaredLicenseContent);
  }

  /**
   * Sets the text pool key of the {@link #getDeclaredLicenseContent()}.
   *
   * @param declaredLicenseContentKey the key
   */
  public void setDeclaredLicenseContentKey(String declaredLicenseContentKey) {

    this.declaredLicenseContentKey = declaredLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public void setEffectiveNormalizedLicense(String effectiveNormalizedLicense) {

    this.effectiveNormalizedLicense = effectiveNormalizedLicense;
  }

  /** {@inheritDoc} */
  @Override
  public void setEffectiveNormalizedLicenseType(String effectiveNormalizedLicenseType) {

    this.effectiveNormalizedLicenseType = effectiveNormalizedLicenseType;
  }

  /**
   * Sets the effectiveNormalizedLicenseContent.
   *
   * @param effectiveNormalizedLicenseContent the content to set
   */
  @Override
  public void setEffectiveNormalizedLicenseContent(String effectiveNormalizedLicenseContent) {

    this.effectiveNormalizedLicenseContentKey = storeTextInPool(effectiveNormalizedLicenseContent);
  }

  /**
   * Sets the text pool key of the {@link #getEffectiveNormalizedLicenseContent()}.
   *
   * @param effectiveNormalizedLicenseContentKey the key
   */
  public void setEffectiveNormalizedLicenseContentKey(String effectiveNormalizedLicenseContentKey) {

    this.effectiveNormalizedLicenseContentKey = effectiveNormalizedLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public void setEffectiveNormalizedLicenseUrl(String effectiveNormalizedLicenseUrl) {

    this.effectiveNormalizedLicenseUrl = effectiveNormalizedLicenseUrl;
  }

  /** {@inheritDoc} */
  @Override
  public void setIncludeLicense(String includeLicense) {

    this.includeLicense = includeLicense;
  }

  /** {@inheritDoc} */
  @Override
  public void setIncludeSource(String includeSource) {

    this.includeSource = includeSource;
  }

  /** {@inheritDoc} */
  @Override
  public void setLegalApproved(String legalApproved) {

    this.legalApproved = legalApproved;
  }

  /** {@inheritDoc} */
  @Override
  public void setLegalComments(String legalComments) {

    this.legalComments = legalComments;
  }

  /** {@inheritDoc} */
  @Override
  public void setLegalPreApproved(String legalPreApproved) {

    this.legalPreApproved = legalPreApproved;
  }

  /** {@inheritDoc} */
  @Override
  public void setLicenseCompliance(String licenseCompliance) {

    this.licenseCompliance = licenseCompliance;
  }

  /**
   * This method sets the field <code>licenseContentProvider</code>.
   *
   * @param licenseContentProvider the new value of the field licenseContentProvider
   */
  public void setLicenseContentProvider(ContentProvider<WebContent> licenseContentProvider) {

    this.licenseContentProvider = licenseContentProvider;
  }

  /**
   * This method sets the field <code>licenseUrlGuesser</code>.
   *
   * @param licenseUrlGuesser the new value of the field icenseUrlGuesser
   */
  public void setLicenseUrlGuesser(ContentProvider<GuessedLicenseUrlContent> licenseUrlGuesser) {

    this.licenseUrlGuesser = licenseUrlGuesser;
  }

  /** {@inheritDoc} */
  @Override
  public void setLicenseRefUrl(String licenseRefUrl) {

    this.licenseRefUrl = licenseRefUrl;
  }

  /**
   * Sets the licenseRefContent.
   *
   * @param licenseRefContent the content to set
   */
  @Override
  public void setLicenseRefContent(String licenseRefContent) {

    this.licenseRefContentKey = storeTextInPool(licenseRefContent);
  }

  /**
   * Sets the text pool key of the {@link #getLicenseRefContent()}.
   *
   * @param licenseRefContentKey the key
   */
  public void setLicenseRefContentKey(String licenseRefContentKey) {

    this.licenseRefContentKey = licenseRefContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public void setLicenseUrl(String licenseUrl) {

    this.licenseUrl = licenseUrl;
  }

  /** {@inheritDoc} */
  @Override
  public void setNormalizedLicense(String normalizedLicense) {

    this.normalizedLicense = normalizedLicense;
  }

  /** {@inheritDoc} */
  @Override
  public void setNormalizedLicenseType(String normalizedLicenseType) {

    this.normalizedLicenseType = normalizedLicenseType;
  }

  /** {@inheritDoc} */
  @Override
  public void setNormalizedLicenseUrl(String normalizedLicenseUrl) {

    this.normalizedLicenseUrl = normalizedLicenseUrl;
  }

  /**
   * Sets the normalizedLicenseContent.
   *
   * @param normalizedLicenseContent the content to set
   */
  @Override
  public void setNormalizedLicenseContent(String normalizedLicenseContent) {

    this.normalizedLicenseContentKey = storeTextInPool(normalizedLicenseContent);
  }

  /**
   * Sets the text pool key of the {@link #getNormalizedLicenseContent()}.
   *
   * @param normalizedLicenseContentKey the key
   */
  public void setNormalizedLicenseContentKey(String normalizedLicenseContentKey) {

    this.normalizedLicenseContentKey = normalizedLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public void setReviewedForRelease(String reviewedForRelease) {

    this.reviewedForRelease = reviewedForRelease;
  }

  /** {@inheritDoc} */
  @Override
  public void setTrace(String trace) {

    this.trace = trace;
  }

  /** {@inheritDoc} */
  @Override
  public void setGuessedLicenseUrl(String guessedLicenseUrl) {

    this.guessedLicenseUrl = guessedLicenseUrl;

  }

  /**
   * Sets the guessedLicenseContent.
   *
   * @param guessedLicenseContent the content to set
   */
  @Override
  public void setGuessedLicenseContent(String guessedLicenseContent) {

    this.guessedLicenseContentKey = storeTextInPool(guessedLicenseContent);
  }

  /**
   * Sets the text pool key of the {@link #getGuessedLicenseContent()}.
   *
   * @param guessedLicenseContentKey the key
   */
  public void setGuessedLicenseContentKey(String guessedLicenseContentKey) {

    this.guessedLicenseContentKey = guessedLicenseContentKey;
  }

  /** {@inheritDoc} */
  @Override
  public void setGuessedLicenseUrlAuditInfo(String guessedLicenseUrlAuditInfo) {

    this.guessedLicenseUrlAuditInfo = guessedLicenseUrlAuditInfo;
  }

  /** {@inheritDoc} */
  @Override
  public void completeData() {

    // following methods try to fill some data fields if they do not yet contain data
    possiblyGuessLicenseUrl();
    possiblyFillGuessedLicenseContent();
    possiblyFillDeclaredLicenseContent();
    possiblyFillNormalizedLicenseContent();
    possiblyFillEffectiveNormalizedLicenseContent();
    possiblyFillLicenseRefContent();

  }

  /**
   * If the {@link #guessedLicenseUrl} is not yet set it will be tried to guess it. This includes also setting the
   * {@link #guessedLicenseUrlAuditInfo}.
   */
  private void possiblyGuessLicenseUrl() {

    // execute license guessing based on effectziveNormalizedLicensUrl
    if (this.guessedLicenseUrl == null) {
      GuessedLicenseUrlContent guessed = this.licenseUrlGuesser.getContentForUri(this.effectiveNormalizedLicenseUrl);
      this.guessedLicenseUrl = guessed.getGuessedUrl();
      this.guessedLicenseUrlAuditInfo = guessed.getAuditInfo();
    }
  }

  /**
   * If the {@link #getGuessedLicenseContent()} is not yet set (i.e. the content is not yet set) it will be attempted to
   * fetch the content via the {@link #licenseContentProvider} and store it.
   */
  private void possiblyFillGuessedLicenseContent() {

    if (getGuessedLicenseContent() == null) {
      setGuessedLicenseContent(this.licenseContentProvider.getContentForUri(this.guessedLicenseUrl).getContent());
    }
  }

  /**
   * If the {@link #getDeclaredLicenseContent} is not yet set(i.e. the content is not set yet) it will be attempted to
   * fetch the content via the {@link #licenseContentProvider} and store it.
   */
  private void possiblyFillDeclaredLicenseContent() {

    if (getDeclaredLicenseContent() == null) {
      setDeclaredLicenseContent(this.licenseContentProvider.getContentForUri(this.licenseUrl).getContent());
    }
  }

  /**
   * If the {@link #getNormalizedLicenseContent()} is not yet set (i.e. the content is not set yet) it will be attempted
   * to fill the content. This is tried via two ways:
   * <ul>
   * <li>if {@link #normalizedLicenseUrl} equals {@link #licenseUrl} the {@link #getDeclaredLicenseContent()} will be
   * taken</li>
   * <li>otherwise the content will be fetched via the {@link #licenseContentProvider}</li>
   * </ul>
   */
  private void possiblyFillNormalizedLicenseContent() {

    if (getNormalizedLicenseContent() == null) {
      if (this.normalizedLicenseUrl != null && this.normalizedLicenseUrl.equals(this.licenseUrl)) {
        setNormalizedLicenseContent(getDeclaredLicenseContent());
      } else {
        setNormalizedLicenseContent(
            this.licenseContentProvider.getContentForUri(this.normalizedLicenseUrl).getContent());
      }
    }
  }

  /**
   * If the {@link #getEffectiveNormalizedLicenseContent()} is not yet set (i.e. the content is not set yet) it will be
   * attempted to fill the content. This is tried via two ways:
   * <ul>
   * <li>if {@link #effectiveNormalizedLicenseUrl} equals {@link #normalizedLicenseUrl} the
   * {@link #getNormalizedLicenseContent()} will be taken</li>
   * <li>otherwise the content will be fetched via the {@link #licenseContentProvider}</li>
   * </ul>
   */
  private void possiblyFillEffectiveNormalizedLicenseContent() {

    if (getEffectiveNormalizedLicenseContent() == null) {
      if (this.effectiveNormalizedLicenseUrl != null
          && this.effectiveNormalizedLicenseUrl.equals(this.normalizedLicenseUrl)) {
        setEffectiveNormalizedLicenseContent(getNormalizedLicenseContent());
      } else {
        setEffectiveNormalizedLicenseContent(
            this.licenseContentProvider.getContentForUri(this.effectiveNormalizedLicenseUrl).getContent());
      }
    }
  }

  /**
   * If the {@link #getLicenseRefContent()} is not yet set(i.e. the content is not set yet) it will be attempted to
   * fetch the content via the {@link #licenseContentProvider} and store it.
   */
  private void possiblyFillLicenseRefContent() {

    if (getLicenseRefContent() == null) {
      setLicenseRefContent(this.licenseContentProvider.getContentForUri(this.licenseRefUrl).getContent());
    }
  }

}
