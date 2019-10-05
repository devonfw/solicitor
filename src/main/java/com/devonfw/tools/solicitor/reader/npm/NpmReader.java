/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.npm;

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
 * A {@link Reader} which reads data produced by the
 * <a href="https://www.npmjs.com/package/npm-license-crawler">NPM License
 * Crawler</a>.
 */
@Component
public class NpmReader extends AbstractReader implements Reader {

    /**
     * The supported type of this {@link Reader}.
     */
    public static final String SUPPORTED_TYPE = "npm";

    /** {@inheritDoc} */
    @Override
    public String getSupportedType() {

        return SUPPORTED_TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public void readInventory(String sourceUrl, Application application, UsagePattern usagePattern) {

        int components = 0;
        int licenses = 0;
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);

            java.io.Reader reader = new InputStreamReader(is);

            ApplicationComponent lastAppComponent = null;
            for (CSVRecord record : CSVFormat.newFormat(',').withQuote('\"').parse(reader)) {
                if (record.get(0).contains("module name")) {
                    continue;
                }

                ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
                String[] module = record.get(0).split("@");
                appComponent.setArtifactId(module[module.length - 2]);
                appComponent.setVersion(module[module.length - 1]);
                appComponent.setUsagePattern(usagePattern);
                appComponent.setGroupId(record.get(4));
                appComponent.setOssHomepage(record.get(2));
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
                    components++;
                    appComponent.setApplication(application);
                    lastAppComponent = appComponent;
                }
                licenses++;
                addRawLicense(lastAppComponent, record.get(1), record.get(3), sourceUrl);
            }
            doLogging(sourceUrl, application, components, licenses);

        } catch (IOException e) {
            throw new SolicitorRuntimeException("Could not read NPM inventory source +'" + sourceUrl + "'", e);
        }
    }

}
