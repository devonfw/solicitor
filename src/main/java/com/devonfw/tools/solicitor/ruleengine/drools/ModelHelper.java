/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.ruleengine.drools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;

/**
 * Uitility class for dealing with the data model.
 */
@Component
public class ModelHelper {
    private static final Logger LOG =
            LoggerFactory.getLogger(ModelHelper.class);

    private static ModelFactory modelFactory;

    /**
     * Private Constructor to avoid instantiation.
     */
    private ModelHelper() {

    }

    /**
     * Saves a reference to the ModelFactory in a static variable to allow
     * access to factory methods via static methods.
     * 
     * @param modelFactory
     */
    @Autowired
    public void setModelFactory(ModelFactory modelFactory) {

        ModelHelper.modelFactory = modelFactory;
    }

    /**
     * Creates a {@link NormalizedLicense} by calling
     * {@link ModelFactory#newNormalizedLicense(RawLicense)}.
     * 
     * @param rawLicense
     * @return the new instance
     */
    public static NormalizedLicense newNormalizedLicense(
            RawLicense rawLicense) {

        return modelFactory.newNormalizedLicense(rawLicense);
    }

    /**
     * Creates a new {@link RawLicense} by calling
     * {@link ModelFactory#newRawLicense()} on the implementation instance.
     * 
     * @return the new instance
     */
    public static RawLicense newRawLicense() {

        return modelFactory.newRawLicense();
    }

    /**
     * Creates a new {@link ApplicationComponent} by calling
     * {@link ModelFactory#newApplicationComponent()}.
     * 
     * @return the new instance
     */
    public static ApplicationComponent newApplicationComponent() {

        return modelFactory.newApplicationComponent();
    }

    /**
     * Creates a new {@link Application} by calling
     * {@link ModelFactory#newApplication(String, String, String, String, String)}.
     * 
     * @param name
     * @param releaseId
     * @param releaseDate
     * @param sourceRepo
     * @param programmingEcosystem
     * @return the new instance
     */
    public static Application newApplication(String name, String releaseId,
            String releaseDate, String sourceRepo,
            String programmingEcosystem) {

        return modelFactory.newApplication(name, releaseId, releaseDate,
                sourceRepo, programmingEcosystem);
    }

    /**
     * Creates a new {@link Engagement} by calling
     * {@link ModelFactory#newEngagement(String, EngagementType, String, GoToMarketModel)}
     * .
     * 
     * @param engagementName
     * @param engagementType
     * @param clientName
     * @param goToMarketModel
     * @return the new instance
     */
    public static Engagement newEngagement(String engagementName,
            EngagementType engagementType, String clientName,
            GoToMarketModel goToMarketModel) {

        return modelFactory.newEngagement(engagementName, engagementType,
                clientName, goToMarketModel);
    }

    /**
     * Adds the given comment to the comments of the given license object.
     * 
     * @param license
     * @param comment
     */
    public static void addCommentToNormalizedLicense(NormalizedLicense license,
            String comment) {

        String comments = license.getComments();

        if (comments == null) {
            comments = comment;
        } else {
            comments = comments + "; " + comment;
        }
        license.setComments(comments);
    }

    /**
     * Adds the given traceEntry to the trace infos of the given license object.
     * 
     * @param license
     * @param traceEntry
     */
    public static void appendTraceToNormalizedLicense(NormalizedLicense license,
            String traceEntry) {

        LOG.debug(traceEntry);
        String trace = license.getTrace();
        if (trace == null) {
            trace = traceEntry;
        } else {
            trace += System.lineSeparator() + traceEntry;
        }
        license.setTrace(trace);
    }

}
