/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.csv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link Reader} for files in CSV format.
 * <p>
 * CSV files need to be configured within the solicitor.cfg and 
 * contain at least following parameters:
 * </p>
 * <ul>
 * <li>artifactID</li>
 * <li>version</li>
 * </ul>
 * <p> Other optional (but recommended) parameters are:
 * <ul>
 * <li>groupID
 * <li>license</li>
 * <li>licenseURL</li>
 * <li>skipheader</li>
 * <li>quote</li>
 * <li>delimiter</li>
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
    	
    	//this is how to get out values of configuration file
    	System.out.println("this are the config parameters given in solicitor.cfg:");
    	System.out.println(configuration.get("groupID"));
    	System.out.println(configuration.get("artifactID"));
    	System.out.println(configuration);
    	System.out.println("\n");

    	//TODO replace config file logic with new configuration.get() methods
    	String sourceEnding = sourceUrl.substring(sourceUrl.lastIndexOf("/") + 1);
		String configPath = sourceUrl.replace(sourceEnding, "csvreader.config");
		configPath = configPath.replace("file:", "");
    	Properties props = new Properties();
    	try (FileInputStream file = new FileInputStream(configPath)) {
    	    props.load(file);
    	} catch (FileNotFoundException ex) {
            throw new SolicitorRuntimeException("Could not find config file '" + configPath + "'", ex);
    	} catch (IOException ex) {
            throw new SolicitorRuntimeException("Could not read config file '" + configPath + "'", ex);
    	}

        int components = 0;
        int licenses = 0;
        InputStream is;
        try {
            is = inputStreamFactory.createInputStreamFor(sourceUrl);

            java.io.Reader reader = new InputStreamReader(is);
            ApplicationComponent lastAppComponent = null;
            
            //TODO apply newest common csv format with csvFormat Builder
            //TODO apply common csv formats like EXCEL as configuration parameter and disable rest if inputted
            //TODO add documentation in solicitor asciidoc
            CSVFormat csvFormat = CSVFormat.newFormat(props.getProperty("delimiter").charAt(0));
            //checks if quote is set in config file
            if(!props.getProperty("quote").isEmpty()) {
                csvFormat = csvFormat.withQuote((props.getProperty("quote")).charAt(0));
            }            
            if(props.getProperty("skipheader").equals("yes")) {
            	csvFormat = csvFormat.withFirstRecordAsHeader().withSkipHeaderRecord();
            }
            
            
            
            for (CSVRecord record : csvFormat.parse(reader)) {
                ApplicationComponent appComponent = getModelFactory().newApplicationComponent();

                //set strings from csv position defined by config
                String groupId = "";
                if(!props.getProperty("groupID").isEmpty()) {
                    groupId = record.get(Integer.parseInt(props.getProperty("groupID")));
                }
                String license ="";
                if(!props.getProperty("license").isEmpty()) {
                    license = record.get(Integer.parseInt(props.getProperty("license")));
                }
                String licenseURL = "";
                if(!props.getProperty("licenseURL").isEmpty()) {
                    licenseURL = record.get(Integer.parseInt(props.getProperty("licenseURL")));
                }
                String artifactId = record.get(Integer.parseInt(props.getProperty("artifactID")));
                String version = record.get(Integer.parseInt(props.getProperty("version")));

                
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
