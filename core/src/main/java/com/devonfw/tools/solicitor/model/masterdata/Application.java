package com.devonfw.tools.solicitor.model.masterdata;

import java.util.List;

import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;

/**
 * Represents an Application in the Solicitor data model.
 */
public interface Application {

    /**
     * Adds an {@link ApplicationComponent} to this Application.
     *
     * @param applicationComponent a {@link ApplicationComponent} object.
     */
    void addApplicationComponent(ApplicationComponent applicationComponent);

    /**
     * This method gets an unmodifiable copy of the field
     * <code>applicationComponents</code>.
     *
     * @return the field applicationComponents
     */
    List<ApplicationComponent> getApplicationComponents();

    /**
     * This method gets the field <code>engagement</code>.
     *
     * @return the field engagement
     */
    Engagement getEngagement();

    /**
     * This method gets the field <code>name</code>.
     *
     * @return the field name
     */
    String getName();

    /**
     * This method gets the field <code>programmingEcosystem</code>.
     *
     * @return the field programmingEcosystem
     */
    String getProgrammingEcosystem();

    /**
     * This method gets the field <code>releaseDate</code>.
     *
     * @return the field releaseDate
     */
    String getReleaseDate();

    /**
     * This method gets the field <code>releaseId</code>.
     *
     * @return the field releaseId
     */
    String getReleaseId();

    /**
     * This method gets the field <code>sourceRepo</code>.
     *
     * @return the field sourceRepo
     */
    String getSourceRepo();

    /**
     * Sets the {@link Engagement} to which this {@link Application} belongs.
     *
     * @param engagement the parent in the data model
     */
    void setEngagement(Engagement engagement);

    /**
     * This method sets the field <code>name</code>.
     *
     * @param name the new value of the field name
     */
    void setName(String name);

    /**
     * This method sets the field <code>programmingEcosystem</code>.
     *
     * @param programmingEcosystem the new value of the field
     *        programmingEcosystem
     */
    void setProgrammingEcosystem(String programmingEcosystem);

    /**
     * This method sets the field <code>releaseDate</code>.
     *
     * @param releaseDate the new value of the field releaseDate
     */
    void setReleaseDate(String releaseDate);

    /**
     * This method sets the field <code>releaseId</code>.
     *
     * @param releaseId the new value of the field releaseId
     */
    void setReleaseId(String releaseId);

    /**
     * This method sets the field <code>sourceRepo</code>.
     *
     * @param sourceRepo the new value of the field sourceRepo
     */
    void setSourceRepo(String sourceRepo);

    /**
     * Complete the data of this object by setting members which are derived
     * from other members.
     */
    public void completeData();

}
