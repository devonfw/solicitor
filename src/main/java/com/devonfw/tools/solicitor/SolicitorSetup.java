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
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

import lombok.Data;

@Component
@Data
public class SolicitorSetup {

    @Data
    public static class ReaderSetup {
        private String type;

        private String source;

        private Application application;

        private UsagePattern usagePattern;
    }

    private Engagement engagement;

    private List<ReaderSetup> readerSetups = new ArrayList<>();

    private List<RuleConfig> ruleSetups = new ArrayList<>();

    private List<WriterConfig> writerSetups = new ArrayList<>();

}
