/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;

/**
 * A {@link Reader} for files in CSV format.
 * <p>
 * CSV files need to be separated with ";" and contain the following 5 columns
 * </p>
 * <ul>
 * <li>groupId</li>
 * <li>artifactId</li>
 * <li>version</li>
 * <li>license name</li>
 * <li>license URL</li>
 * </ul>
 */
@Component
public class CsvReader extends AbstractReader implements Reader {

    /**
     * The supported type of this {@link Reader}.
     */
    public static final String SUPPORTED_TYPE = "csv";

    /** {@inheritDoc} */
    @Override
    public String getSupportedType() {

        return SUPPORTED_TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public void readInventory(String sourceUrl, Application application, UsagePattern usagePattern, String repoType) {

        int components = 0;
        int licenses = 0;
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);

            java.io.Reader reader = new InputStreamReader(is);
            ApplicationComponent lastAppComponent = null;
            for (CSVRecord record : CSVFormat.newFormat(';').parse(reader)) {
                ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
                appComponent.setGroupId(record.get(0));
                appComponent.setArtifactId(record.get(1));
                appComponent.setVersion(record.get(2));
                appComponent.setUsagePattern(usagePattern);
                appComponent.setRepoType(repoType);
                // merge ApplicationComponentImpl with same key if they appear
                // on
                // subsequent lines (multilicensing)
                if (lastAppComponent != null && lastAppComponent.getGroupId().equals(appComponent.getGroupId())
                        && lastAppComponent.getArtifactId().equals(appComponent.getArtifactId())
                        && lastAppComponent.getVersion().equals(appComponent.getVersion())) {
                    // same applicationComponent as previous line ->
                    // append rawLicense to already existing
                    // ApplicationComponent
                } else {
                    // new ApplicationComponentImpl
                    appComponent.setApplication(application);
                    lastAppComponent = appComponent;
                    components++;
                }
                licenses++;
                addRawLicense(lastAppComponent, record.get(3), record.get(4), sourceUrl);
            }
            doLogging(sourceUrl, application, components, licenses);
        } catch (IOException e1) {
            throw new SolicitorRuntimeException("Could not read CSV inventory source '" + sourceUrl + "'", e1);
        }

    }

}
