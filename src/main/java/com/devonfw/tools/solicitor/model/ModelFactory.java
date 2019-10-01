package com.devonfw.tools.solicitor.model;

import java.util.Collection;

import com.devonfw.tools.solicitor.common.AbstractDataRowSource;
import com.devonfw.tools.solicitor.model.impl.masterdata.EngagementImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.EngagementType;
import com.devonfw.tools.solicitor.model.masterdata.GoToMarketModel;

public abstract class ModelFactory {

    /**
     * Creates a {@link NormalizedLicense}.
     * 
     * @return the new instance
     */
    public abstract NormalizedLicense newNormalizedLicense();

    /**
     * Creates a {@link NormalizedLicense}.
     * 
     * @param rawLicense
     * @return the new instance
     */
    public abstract NormalizedLicense newNormalizedLicense(
            RawLicense rawLicense);

    /**
     * Creates a new {@link RawLicense}
     * 
     * @return the new instance
     */
    public abstract RawLicense newRawLicense();

    /**
     * Creates a new {@link ApplicationComponent}
     * 
     * @return the new instance
     */
    public abstract ApplicationComponent newApplicationComponent();

    /**
     * Creates a new {@link Application}
     * 
     * @param name
     * @param releaseId
     * @param releaseDate
     * @param sourceRepo
     * @param programmingEcosystem
     * @return the new instance
     */
    public abstract Application newApplication(String name, String releaseId,
            String releaseDate, String sourceRepo, String programmingEcosystem);

    /**
     * Creates a new {@link Engagement}
     * 
     * @param engagementName
     * @param engagementType
     * @param clientName
     * @param goToMarketModel
     * @return the new instance
     */
    public abstract Engagement newEngagement(String engagementName,
            EngagementType engagementType, String clientName,
            GoToMarketModel goToMarketModel);

    /**
     * Creates a {@link ModelRoot}.
     * 
     * @return the new instance
     */
    public abstract ModelRoot newModelRoot();

    /**
     * Returns the collection of all {@link AbstractDataRowSource} objects of
     * the model tree starting from the given {@link ModelRoot}. All model
     * objects must be instances of the implementation classes for the model
     * objects as used / created by this model factory.
     * 
     * @param engagementthe root of the model tree; needs to ba an instance of
     *        {@link EngagementImpl}.
     * @return a collection of {@link AbstractDataRowSource} representing all
     *         objects of the model
     */
    public abstract Collection<Object> getAllModelObjects(ModelRoot modelRoot);

}