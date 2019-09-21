/**
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
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;

@Component
public class CsvReader extends AbstractReader implements Reader {

    @Autowired
    private InputStreamFactory inputStreamFactory;

    @Override
    public String getSupportedType() {

        return "csv";
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
                ApplicationComponent appComponent =
                        getModelFactory().newApplicationComponent();
                appComponent.setGroupId(record.get(0));
                appComponent.setArtifactId(record.get(1));
                appComponent.setVersion(record.get(2));
                appComponent.setUsagePattern(usagePattern);
                // merge ApplicationComponentImpl with same key if they appear
                // on
                // subsequent lines (multilicensing)
                if (lastAppComponent != null
                        && lastAppComponent.getGroupId()
                                .equals(appComponent.getGroupId())
                        && lastAppComponent.getArtifactId()
                                .equals(appComponent.getArtifactId())
                        && lastAppComponent.getVersion()
                                .equals(appComponent.getVersion())) {
                    // same applicationComponent as previous line ->
                    // append rawLicense to already existing
                    // ApplicationComponent
                } else {
                    // new ApplicationComponentImpl
                    appComponent.setApplication(application);
                    lastAppComponent = appComponent;
                }
                addRawLicense(lastAppComponent, record.get(3), record.get(4),
                        sourceUrl);
            }
        } catch (IOException e1) {
            throw new SolicitorRuntimeException(
                    "Could not read CSV inventory source +'" + sourceUrl + "'",
                    e1);
        }

    }

    /**
     * This method sets the field <tt>inputStreamFactory</tt>.
     *
     * @param inputStreamFactory the new value of the field inputStreamFactory
     */
    public void setInputStreamFactory(InputStreamFactory inputStreamFactory) {

        this.inputStreamFactory = inputStreamFactory;
    }

}
