/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.capgemini.solicitor.config.RuleConfig;
import com.capgemini.solicitor.config.WriterConfig;
import com.capgemini.solicitor.model.masterdata.Application;
import com.capgemini.solicitor.model.masterdata.Engagement;
import com.capgemini.solicitor.model.masterdata.UsagePattern;

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
