/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.config.RuleConfig;
import com.devonfw.tools.solicitor.config.WriterConfig;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.Reader;

/**
 * Holder for the Solicitor configuration.
 */
@Component
public class SolicitorSetup {

    /**
     * Holder for the setup of a {@link Reader}.
     */
    public static class ReaderSetup {
        private String type;

        private String source;

        private Application application;

        private UsagePattern usagePattern;

        /**
         * This method gets the field <tt>application</tt>.
         *
         * @return the field application
         */
        public Application getApplication() {

            return application;
        }

        /**
         * This method gets the field <tt>source</tt>.
         *
         * @return the field source
         */
        public String getSource() {

            return source;
        }

        /**
         * This method gets the field <tt>type</tt>.
         *
         * @return the field type
         */
        public String getType() {

            return type;
        }

        /**
         * This method gets the field <tt>usagePattern</tt>.
         *
         * @return the field usagePattern
         */
        public UsagePattern getUsagePattern() {

            return usagePattern;
        }

        /**
         * This method sets the field <tt>application</tt>.
         *
         * @param application the new value of the field application
         */
        public void setApplication(Application application) {

            this.application = application;
        }

        /**
         * This method sets the field <tt>source</tt>.
         *
         * @param source the new value of the field source
         */
        public void setSource(String source) {

            this.source = source;
        }

        /**
         * This method sets the field <tt>type</tt>.
         *
         * @param type the new value of the field type
         */
        public void setType(String type) {

            this.type = type;
        }

        /**
         * This method sets the field <tt>usagePattern</tt>.
         *
         * @param usagePattern the new value of the field usagePattern
         */
        public void setUsagePattern(UsagePattern usagePattern) {

            this.usagePattern = usagePattern;
        }
    }

    private List<ReaderSetup> readerSetups = new ArrayList<>();

    private List<RuleConfig> ruleSetups = new ArrayList<>();

    private List<WriterConfig> writerSetups = new ArrayList<>();

    /**
     * This method gets the field <tt>readerSetups</tt>.
     *
     * @return the field readerSetups
     */
    public List<ReaderSetup> getReaderSetups() {

        return readerSetups;
    }

    /**
     * This method gets the field <tt>ruleSetups</tt>.
     *
     * @return the field ruleSetups
     */
    public List<RuleConfig> getRuleSetups() {

        return ruleSetups;
    }

    /**
     * This method gets the field <tt>writerSetups</tt>.
     *
     * @return the field writerSetups
     */
    public List<WriterConfig> getWriterSetups() {

        return writerSetups;
    }

    /**
     * This method sets the field <tt>readerSetups</tt>.
     *
     * @param readerSetups the new value of the field readerSetups
     */
    public void setReaderSetups(List<ReaderSetup> readerSetups) {

        this.readerSetups = readerSetups;
    }

    /**
     * This method sets the field <tt>ruleSetups</tt>.
     *
     * @param ruleSetups the new value of the field ruleSetups
     */
    public void setRuleSetups(List<RuleConfig> ruleSetups) {

        this.ruleSetups = ruleSetups;
    }

    /**
     * This method sets the field <tt>writerSetups</tt>.
     *
     * @param writerSetups the new value of the field writerSetups
     */
    public void setWriterSetups(List<WriterConfig> writerSetups) {

        this.writerSetups = writerSetups;
    }

}
