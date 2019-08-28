/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.Reader;

import lombok.Setter;

@Component
public class CsvReader implements Reader {

    @Autowired
    @Setter
    private InputStreamFactory inputStreamFactory;

    @Override
    public boolean accept(String type) {

        return "csv".equals(type);
    }

    @Override
    public void readInventory(String sourceUrl, Application application,
            UsagePattern usagePattern) {

        // File file = new File(source);
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);

            java.io.Reader reader = new InputStreamReader(is);
            ApplicationComponent lastAppComponent = null;
            for (CSVRecord record : CSVFormat.newFormat(';').parse(reader)) {
                ApplicationComponent appComponent = new ApplicationComponent();
                appComponent.setGroupId(record.get(0));
                appComponent.setArtifactId(record.get(1));
                appComponent.setVersion(record.get(2));
                appComponent.setUsagePattern(usagePattern);
                // merge ApplicationComponent with same key if they appear on
                // subsequent lines (multilicensing)
                if (lastAppComponent != null && lastAppComponent.getKey()
                        .equals(appComponent.getKey())) {
                    // same applicationComponent as previous line -> append
                    // rawLicense to already existing ApplicationComponent
                } else {
                    // new ApplicationComponent
                    appComponent.setApplication(application);
                    lastAppComponent = appComponent;
                }
                RawLicense mlic = new RawLicense();
                mlic.setApplicationComponent(lastAppComponent);
                mlic.setDeclaredLicense(record.get(3));
                mlic.setLicenseUrl(record.get(4));

            }
        } catch (IOException e1) {
            throw new SolicitorRuntimeException(
                    "Could not read CSV inventory source +'" + sourceUrl + "'",
                    e1);
        }

    }

}
