/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.model;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Imports and exports the data model.
 */
@Component
public class ModelImporterExporter {
    private static final Logger LOG = LoggerFactory.getLogger(ModelImporterExporter.class);

    @Autowired
    private ModelFactory modelFactory;

    /**
     * Loads the data model from a file.
     *
     * @param filename the name of the file to load from.
     * @return the root object of the loaded data model
     */
    public ModelRoot loadModel(String filename) {

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            JsonNode root = objectMapper.readTree(new File(filename));
            int modelVersion = root.get("modelVersion").asInt();
            ModelRoot modelRoot = modelFactory.newModelRoot();
            if (modelVersion != modelRoot.getModelVersion()) {
                throw new SolicitorRuntimeException("Unsupported model version " + modelVersion + " can not be loaded");
            }
            String executionTime = root.get("executionTime").asText();
            String solicitorVersion = root.get("solicitorVersion").asText();
            String solicitorGitHash = root.get("solicitorGitHash").asText();
            String solicitorBuilddate = root.get("solicitorBuilddate").asText();
            String extensionArtifactId = root.get("extensionArtifactId").asText();
            String extensionVersion = root.get("extensionVersion").asText();
            String extensionGitHash = root.get("extensionGitHash").asText();
            String extensionBuilddate = root.get("extensionBuilddate").asText();
            JsonNode engagementNode = root.get("engagement");
            modelRoot.setExecutionTime(executionTime);
            modelRoot.setSolicitorVersion(solicitorVersion);
            modelRoot.setSolicitorGitHash(solicitorGitHash);
            modelRoot.setSolicitorBuilddate(solicitorBuilddate);
            modelRoot.setExtensionArtifactId(extensionArtifactId);
            modelRoot.setExtensionVersion(extensionVersion);
            modelRoot.setExtensionGitHash(extensionGitHash);
            modelRoot.setExtensionBuilddate(extensionBuilddate);
            readEngagement(modelRoot, engagementNode);
            return modelRoot;
        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not load internal data model from file '" + filename + "'", e);
        }

    }

    /**
     * Read the {@link ApplicationComponent}s from the JSON data structure.
     * 
     * @param application the {@link Application} to which the
     *        {@link ApplicationComponent}s belong to
     * @param applicationComponentsNode the relevant part of the parse JSON
     *        model
     */
    private void readApplicationComponents(Application application, JsonNode applicationComponentsNode) {

        for (JsonNode applicationComponentNode : applicationComponentsNode) {
            String usagePattern = applicationComponentNode.get("usagePattern").asText(null);
            boolean ossModified = applicationComponentNode.get("ossModified").asBoolean();
            String ossHomepage = applicationComponentNode.get("ossHomepage").asText(null);
            String groupId = applicationComponentNode.get("groupId").asText(null);
            String artifactId = applicationComponentNode.get("artifactId").asText(null);
            String version = applicationComponentNode.get("version").asText(null);
            JsonNode normalizedLicensesNode = applicationComponentNode.get("normalizedLicenses");
            JsonNode rawLicensesNode = applicationComponentNode.get("rawLicenses");

            ApplicationComponent applicationComponent = modelFactory.newApplicationComponent();
            applicationComponent.setApplication(application);
            applicationComponent.setUsagePattern(UsagePattern.valueOf(usagePattern));
            applicationComponent.setOssModified(ossModified);
            applicationComponent.setOssHomepage(ossHomepage);
            applicationComponent.setGroupId(groupId);
            applicationComponent.setArtifactId(artifactId);
            applicationComponent.setVersion(version);

            readNormalizedLicenses(applicationComponent, normalizedLicensesNode);
            readRawLicenses(applicationComponent, rawLicensesNode);

        }

    }

    /**
     * Read the {@link Application}s from the JSON data structure.
     * 
     * @param engagement the {@link Engagement} to which the
     *        {@link Application}s belong to
     * @param applicationsNode the relevant part of the parsed JSON model
     */
    private void readApplications(Engagement engagement, JsonNode applicationsNode) {

        for (JsonNode applicationNode : applicationsNode) {
            String name = applicationNode.get("name").asText(null);
            String releaseId = applicationNode.get("releaseId").asText(null);
            String releaseDate = applicationNode.get("releaseDate").asText(null);
            String sourceRepo = applicationNode.get("sourceRepo").asText(null);
            String programmingEcosystem = applicationNode.get("programmingEcosystem").asText(null);
            JsonNode applicationComponentsNode = applicationNode.get("applicationComponents");
            Application application =
                    modelFactory.newApplication(name, releaseId, releaseDate, sourceRepo, programmingEcosystem);
            application.setEngagement(engagement);
            readApplicationComponents(application, applicationComponentsNode);

        }

    }

    /**
     * Read the {@link Engagement} from the JSON data structure.
     * 
     * @param modelRoot the root object of the data model to which the
     *        {@link Engagement} should be added
     * @param engagementNode the relevant part of the parsed JSON model
     */
    private void readEngagement(ModelRoot modelRoot, JsonNode engagementNode) {

        String engagementName = engagementNode.get("engagementName").asText(null);
        String engagementType = engagementNode.get("engagementType").asText(null);
        String clientName = engagementNode.get("clientName").asText(null);
        String goToMarketModel = engagementNode.get("goToMarketModel").asText(null);
        boolean contractAllowsOss = engagementNode.get("contractAllowsOss").asBoolean();
        boolean ossPolicyFollowed = engagementNode.get("ossPolicyFollowed").asBoolean();
        boolean customerProvidesOss = engagementNode.get("customerProvidesOss").asBoolean();
        JsonNode applicationsNode = engagementNode.get("applications");

        Engagement engagement = modelFactory.newEngagement(engagementName, EngagementType.valueOf(engagementType),
                clientName, GoToMarketModel.valueOf(goToMarketModel));
        engagement.setModelRoot(modelRoot);
        engagement.setContractAllowsOss(contractAllowsOss);
        engagement.setOssPolicyFollowed(ossPolicyFollowed);
        engagement.setCustomerProvidesOss(customerProvidesOss);
        readApplications(engagement, applicationsNode);
    }

    /**
     * Read the {@link NormalizedLicense}s from the JSON data structure.
     * 
     * @param applicationComponent The {@link ApplicationComponent} to which the
     *        license belongs
     * @param normalizedLicensesNode the relevant part of the parsed JSON model
     */
    private void readNormalizedLicenses(ApplicationComponent applicationComponent, JsonNode normalizedLicensesNode) {

        for (JsonNode normalizedLicenseNode : normalizedLicensesNode) {
            String declaredLicense = normalizedLicenseNode.get("declaredLicense").asText(null);
            String licenseUrl = normalizedLicenseNode.get("licenseUrl").asText(null);
            String normalizedLicenseType = normalizedLicenseNode.get("normalizedLicenseType").asText(null);
            String normalizedLicenseS = normalizedLicenseNode.get("normalizedLicense").asText(null);
            String normalizedLicenseUrl = normalizedLicenseNode.get("normalizedLicenseUrl").asText(null);
            String effectiveNormalizedLicenseType =
                    normalizedLicenseNode.get("effectiveNormalizedLicenseType").asText(null);
            String effectiveNormalizedLicense = normalizedLicenseNode.get("effectiveNormalizedLicense").asText(null);
            String effectiveNormalizedLicenseUrl =
                    normalizedLicenseNode.get("effectiveNormalizedLicenseUrl").asText(null);
            String legalPreApproved = normalizedLicenseNode.get("legalPreApproved").asText(null);
            String copyLeft = normalizedLicenseNode.get("copyLeft").asText(null);
            String licenseCompliance = normalizedLicenseNode.get("licenseCompliance").asText(null);
            String licenseRefUrl = normalizedLicenseNode.get("licenseRefUrl").asText(null);
            String includeLicense = normalizedLicenseNode.get("includeLicense").asText(null);
            String includeSource = normalizedLicenseNode.get("includeSource").asText(null);
            String reviewedForRelease = normalizedLicenseNode.get("reviewedForRelease").asText(null);
            String comments = normalizedLicenseNode.get("comments").asText(null);
            String legalApproved = normalizedLicenseNode.get("legalApproved").asText(null);
            String legalComments = normalizedLicenseNode.get("legalComments").asText(null);
            String trace = normalizedLicenseNode.get("trace").asText(null);

            NormalizedLicense normalizedLicense = modelFactory.newNormalizedLicense();
            normalizedLicense.setApplicationComponent(applicationComponent);
            normalizedLicense.setDeclaredLicense(declaredLicense);
            normalizedLicense.setLicenseUrl(licenseUrl);
            normalizedLicense.setNormalizedLicenseType(normalizedLicenseType);
            normalizedLicense.setNormalizedLicense(normalizedLicenseS);
            normalizedLicense.setNormalizedLicenseUrl(normalizedLicenseUrl);
            normalizedLicense.setEffectiveNormalizedLicenseType(effectiveNormalizedLicenseType);
            normalizedLicense.setEffectiveNormalizedLicense(effectiveNormalizedLicense);
            normalizedLicense.setEffectiveNormalizedLicenseUrl(effectiveNormalizedLicenseUrl);
            normalizedLicense.setLegalPreApproved(legalPreApproved);
            normalizedLicense.setCopyLeft(copyLeft);
            normalizedLicense.setLicenseCompliance(licenseCompliance);
            normalizedLicense.setLicenseRefUrl(licenseRefUrl);
            normalizedLicense.setIncludeLicense(includeLicense);
            normalizedLicense.setIncludeSource(includeSource);
            normalizedLicense.setReviewedForRelease(reviewedForRelease);
            normalizedLicense.setComments(comments);
            normalizedLicense.setLegalApproved(legalApproved);
            normalizedLicense.setLegalComments(legalComments);
            normalizedLicense.setTrace(trace);
        }
    }

    /**
     * Read the {@link RawLicense}s from the JSON data structure.
     * 
     * @param applicationComponent The {@link ApplicationComponent} to which the
     *        license belong
     * @param rawLicensesNode the relevant part of the parsed JSON model
     */
    private void readRawLicenses(ApplicationComponent applicationComponent, JsonNode rawLicensesNode) {

        for (JsonNode rawLicenseNode : rawLicensesNode) {
            String declaredLicense = rawLicenseNode.get("declaredLicense").asText(null);
            String licenseUrl = rawLicenseNode.get("licenseUrl").asText(null);
            String trace = rawLicenseNode.get("trace").asText(null);
            boolean specialHandling = rawLicenseNode.get("specialHandling").asBoolean();

            RawLicense rawLicense = modelFactory.newRawLicense();
            rawLicense.setApplicationComponent(applicationComponent);
            rawLicense.setDeclaredLicense(declaredLicense);
            rawLicense.setLicenseUrl(licenseUrl);
            rawLicense.setTrace(trace);
            rawLicense.setSpecialHandling(specialHandling);

        }
    }

    /**
     * Saves the model to a file.
     *
     * @param filename the path/name of the file to save to. If
     *        <code>node</code> a filename in the current directory will be
     *        autocreated.
     * @param modelRoot a {@link ModelRoot} object.
     */
    public void saveModel(ModelRoot modelRoot, String filename) {

        String effectiveFilename = (filename != null) ? filename : "solicitor_" + System.currentTimeMillis() + ".json";
        IOHelper.checkAndCreateLocation(effectiveFilename);
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(effectiveFilename), modelRoot);
        } catch (IOException e) {
            LOG.error("Could not write internal data model to file '{}'", effectiveFilename, e);
        }

    }
}
