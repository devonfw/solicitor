/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorVersion;
import com.devonfw.tools.solicitor.common.content.InMemoryMapContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;
import com.devonfw.tools.solicitor.licensetexts.GuessedLicenseUrlContent;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.impl.inventory.ApplicationComponentImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.NormalizedLicenseImpl;
import com.devonfw.tools.solicitor.model.impl.inventory.RawLicenseImpl;
import com.devonfw.tools.solicitor.model.impl.masterdata.ApplicationImpl;
import com.devonfw.tools.solicitor.model.impl.masterdata.EngagementImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;

/**
 * Implementation of the {@link ModelFactory} interface. All model object created by this factory will be extensions of
 * {@link AbstractModelObject}.
 */
@Component
public class ModelFactoryImpl extends ModelFactory {
  private static final Logger LOG = LoggerFactory.getLogger(ModelFactoryImpl.class);

  @Autowired
  private InMemoryMapContentProvider<WebContent> licenseContentProvider;

  @Autowired
  private InMemoryMapContentProvider<GuessedLicenseUrlContent> licenseUrlGuesser;

  @Autowired
  private SolicitorVersion solicitorVersion;

  /** {@inheritDoc} */
  @Override
  public Collection<Object> getAllModelObjects(ModelRoot modelRoot) {

    Map<String, AbstractModelObject> resultMap = new TreeMap<>();
    ModelRootImpl mr = (ModelRootImpl) modelRoot;
    resultMap.put(mr.getId(), mr);

    EngagementImpl eg = (EngagementImpl) modelRoot.getEngagement();
    resultMap.put(eg.getId(), eg);
    for (Application application : eg.getApplications()) {
      ApplicationImpl ap = (ApplicationImpl) application;
      resultMap.put(ap.getId(), ap);
      for (ApplicationComponent applicationComponent : ap.getApplicationComponents()) {
        ApplicationComponentImpl ac = (ApplicationComponentImpl) applicationComponent;
        resultMap.put(ac.getId(), ac);
        for (RawLicense rawLicense : ac.getRawLicenses()) {
          RawLicenseImpl rl = (RawLicenseImpl) rawLicense;
          resultMap.put(rl.getId(), rl);
        }
        for (NormalizedLicense normalizedLicense : ac.getNormalizedLicenses()) {
          NormalizedLicenseImpl nl = (NormalizedLicenseImpl) normalizedLicense;
          resultMap.put(nl.getId(), nl);
        }
      }
    }
    return Collections.unmodifiableCollection(resultMap.values());
  }

  /** {@inheritDoc} */
  @Override
  public ApplicationImpl newApplication(String name, String releaseId, String releaseDate, String sourceRepo,
      String programmingEcosystem) {

    return new ApplicationImpl(name, releaseId, releaseDate, sourceRepo, programmingEcosystem);
  }

  /** {@inheritDoc} */
  @Override
  public ApplicationComponentImpl newApplicationComponent() {

    ApplicationComponentImpl result = new ApplicationComponentImpl();
    result.setLicenseContentProvider(this.licenseContentProvider);
    return result;

  }

  /** {@inheritDoc} */
  @Override
  public EngagementImpl newEngagement(String engagementName, EngagementType engagementType, String clientName,
      GoToMarketModel goToMarketModel) {

    return new EngagementImpl(engagementName, engagementType, clientName, goToMarketModel);
  }

  /** {@inheritDoc} */
  @Override
  public ModelRootImpl newModelRoot() {

    ModelRootImpl modelRoot = new ModelRootImpl();
    modelRoot.setSolicitorVersion(this.solicitorVersion.getVersion());
    modelRoot.setSolicitorGitHash(this.solicitorVersion.getGithash());
    modelRoot.setSolicitorBuilddate(this.solicitorVersion.getBuilddate());
    modelRoot.setExtensionArtifactId(this.solicitorVersion.getExtensionArtifact());
    modelRoot.setExtensionVersion(this.solicitorVersion.getExtensionVersion());
    modelRoot.setExtensionGitHash(this.solicitorVersion.getExtensionGithash());
    modelRoot.setExtensionBuilddate(this.solicitorVersion.getExtensionBuilddate());
    return modelRoot;
  }

  /** {@inheritDoc} */
  @Override
  public NormalizedLicenseImpl newNormalizedLicense() {

    NormalizedLicenseImpl result = new NormalizedLicenseImpl();
    result.setLicenseContentProvider(this.licenseContentProvider);
    result.setLicenseUrlGuesser(this.licenseUrlGuesser);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public NormalizedLicenseImpl newNormalizedLicense(RawLicense rawLicense) {

    NormalizedLicenseImpl result = new NormalizedLicenseImpl(rawLicense);
    result.setLicenseContentProvider(this.licenseContentProvider);
    result.setLicenseUrlGuesser(this.licenseUrlGuesser);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public RawLicenseImpl newRawLicense() {

    return new RawLicenseImpl();
  }

}
