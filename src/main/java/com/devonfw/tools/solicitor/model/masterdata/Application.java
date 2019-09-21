package com.devonfw.tools.solicitor.model.masterdata;

import java.util.List;

import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;

public interface Application {

    void setEngagement(Engagement engagement);

    /**
     * This method gets the field <tt>name</tt>.
     *
     * @return the field name
     */
    String getName();

    /**
     * This method sets the field <tt>name</tt>.
     *
     * @param name the new value of the field name
     */
    void setName(String name);

    /**
     * This method gets the field <tt>releaseId</tt>.
     *
     * @return the field releaseId
     */
    String getReleaseId();

    /**
     * This method sets the field <tt>releaseId</tt>.
     *
     * @param releaseId the new value of the field releaseId
     */
    void setReleaseId(String releaseId);

    /**
     * This method gets the field <tt>releaseDate</tt>.
     *
     * @return the field releaseDate
     */
    String getReleaseDate();

    /**
     * This method sets the field <tt>releaseDate</tt>.
     *
     * @param releaseDate the new value of the field releaseDate
     */
    void setReleaseDate(String releaseDate);

    /**
     * This method gets the field <tt>sourceRepo</tt>.
     *
     * @return the field sourceRepo
     */
    String getSourceRepo();

    /**
     * This method sets the field <tt>sourceRepo</tt>.
     *
     * @param sourceRepo the new value of the field sourceRepo
     */
    void setSourceRepo(String sourceRepo);

    /**
     * This method gets the field <tt>programmingEcosystem</tt>.
     *
     * @return the field programmingEcosystem
     */
    String getProgrammingEcosystem();

    /**
     * This method sets the field <tt>programmingEcosystem</tt>.
     *
     * @param programmingEcosystem the new value of the field
     *        programmingEcosystem
     */
    void setProgrammingEcosystem(String programmingEcosystem);

    /**
     * This method gets an unmodifiable copy of the field
     * <tt>applicationComponents</tt>.
     *
     * @return the field applicationComponents
     */
    List<ApplicationComponent> getApplicationComponents();

    /**
     * Adds an {@link ApplicationComponent} to this Application.
     *
     * @param applicationComponent
     */
    void addApplicationComponent(ApplicationComponent applicationComponent);

    /**
     * This method gets the field <tt>engagement</tt>.
     *
     * @return the field engagement
     */
    Engagement getEngagement();

}