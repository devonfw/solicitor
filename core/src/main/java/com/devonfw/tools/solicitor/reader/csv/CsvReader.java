/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
 * CSV files need to be configured within the solicitor.cfg and 
 * contain at least following parameters:
 * </p>
 * <ul>
 * <li>artifactId</li>
 * <li>version</li>
 * </ul>
 * <p> Other optional (but recommended) parameters are:
 * <ul>
 * <li>groupId</li>
 * <li>license</li>
 * <li>licenseURL</li>
 * <li>skipheader</li>
 * <li>quote</li>
 * <li>delimiter</li>
 * <li>format</li>
 * <li>charset</li>
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
    public Set<String> getSupportedTypes() {

        return Collections.singleton(SUPPORTED_TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
            String repoType, Map<String,String> configuration) {
    	
        int components = 0;
        int licenses = 0;
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);
            java.io.Reader reader;
            
            if(configuration.get("charset") != null) {
                 reader = new InputStreamReader(is, configuration.get("charset"));
	        }else {
	             reader = new InputStreamReader(is);
	        }   

            ApplicationComponent lastAppComponent = null;
            
            //TODO add documentation in solicitor asciidoc
            CSVFormat csvFormat;
            CSVFormat.Builder csvBuilder;
            
            //predefined format + lets us overwrite values
            if(configuration.get("format") != null) {
            	csvFormat = CSVFormat.valueOf(configuration.get("format"));
                csvBuilder = CSVFormat.Builder.create(csvFormat);
            }else {
                csvBuilder = CSVFormat.Builder.create();
            }      
            
            if(configuration.get("delimiter") != null) {
            csvBuilder.setDelimiter(configuration.get("delimiter"));
            }
            if(configuration.get("quote") != null) {
            	char[] quoteChar = configuration.get("quote").toCharArray();
            	csvBuilder.setQuote(quoteChar[0]);
            }
            if(configuration.get("skipheader") != null) {
	            if(configuration.get("skipheader").equals("yes")) {
	            	csvBuilder.setHeader().setSkipHeaderRecord(true);
	            }
            }
            csvFormat = csvBuilder.build(); 	    

            
            for (CSVRecord record : csvFormat.parse(reader)) {
                ApplicationComponent appComponent = getModelFactory().newApplicationComponent();

                //set strings from csv position defined by config
                String groupId = "";
                if(configuration.get("groupId") != null) {
                    groupId = record.get(Integer.parseInt(configuration.get("groupId")));
                }
                String license ="";
                if(configuration.get("license") != null) {
                    license = record.get(Integer.parseInt(configuration.get("license")));
                }
                String licenseURL = "";
                if(configuration.get("licenseUrl") != null) {
                    licenseURL = record.get(Integer.parseInt(configuration.get("licenseUrl")));
                }
                String artifactId = record.get(Integer.parseInt(configuration.get("artifactId")));
                String version = record.get(Integer.parseInt(configuration.get("version")));
                
                
                appComponent.setGroupId(groupId);
                appComponent.setArtifactId(artifactId);
                appComponent.setVersion(version);
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
                
                addRawLicense(lastAppComponent, license, licenseURL, sourceUrl);
            }
            doLogging(sourceUrl, application, components, licenses);
        } catch (IOException e1) {
            throw new SolicitorRuntimeException("Could not read CSV inventory source '" + sourceUrl + "'", e1);
        }

    }
    
}
