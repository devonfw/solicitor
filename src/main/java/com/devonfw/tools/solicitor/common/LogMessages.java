/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common;

import java.text.DecimalFormat;

/**
 * Enum which defines all log messages of levels INFO and higher.
 */
public enum LogMessages {

    CALLED(0, "Solicitor called with command line arguments: '{}'"), //
    STARTING(1, "Solicitor starts, Version:{}, Buildnumber:{},  Builddate:{}"), //
    COMPLETED(2, "Solicitor processing completed in {} ms"), //
    COPYING_RESOURCE(3, "Copying resource '{}' to file '{}'"), //
    READING_CONFIG(4, "Reading Solicitor configuration from resource '{}'"), //
    CREATING_ENGAGEMENT(5, "Defining Engagement '{}' in Solicitor data model"), //
    CREATING_APPLICATION(6, "Defining Application '{}' in Solicitor data model"), //
    LOADING_DATAMODEL(7, "Loading Solicitor data model from '{}' (overwriting any possibly existing data)"), //
    LOADING_DIFF(8, "Loading old Solicitor data model for performing difference report from '{}'"), //
    SAVING_DATAMODEL(9, "Saving Solicitor data model to '{}'"), //
    READING_INVENTORY(10, "Reading {} ApplicationComponents / {} Licenses for Application '{}' from '{}'"), //
    LOAD_RULES(11, "Loading Rules of type '{}' from source '{}' with template '{}' for Agenda Group '{}'"), //
    ADDING_FACTS(12, "{} Facts have been added to the Drools working memory, staring Rule Engine ..."), //
    RULE_ENGINE_FINISHED(13, "Rule Engine processing completed, {} rules have been fired"), //
    PREPARING_FOR_WRITER(14, "Preparing to write report with writer '{}' using template '{}' to file '{}'"), //
    FINISHED_WRITER(15, "Finished writing report with writer '{}' using template '{}' to file '{}'"), //
    INIT_SQL(16, "Initializing SQL reporting database with Solicitor model data"), //
    INIT_SQL_OLD(17, "Initializing SQL reporting database with OLD Solicitor model data"), //
    EXECUTE_SQL(18, "Creating data of result table '{}' by executing SQL statement given in '{}'"), //
    CREATING_DIFF(19, "Calculating DIFF information for result table '{}'");

    private final String completeMessage;

    /**
     * Private constructor.
     * 
     * @param code the numeric message code
     * @param message the log message; might contain placeholders in logback
     *        format
     */
    private LogMessages(int code, String message) {

        completeMessage = "[SOLI-" + (new DecimalFormat("000")).format(code) + "] " + message;
    }

    /**
     * Gets the complete message.
     * 
     * @return the complete message
     */
    public String msg() {

        return completeMessage;
    }

}
