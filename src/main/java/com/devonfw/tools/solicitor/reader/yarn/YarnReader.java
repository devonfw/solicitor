/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader.yarn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A {@link Reader} which reads data generated by
 * <a href="https://classic.yarnpkg.com/en/docs/cli/licenses/">Yarn</a>.
 */

@Component
public class YarnReader extends AbstractReader implements Reader {

    /**
     * The supported type of this {@link Reader}.
     */
    public static final String SUPPORTED_TYPE = "yarn";

    /** {@inheritDoc} */
    @Override
    public Set<String> getSupportedTypes() {

        return Collections.singleton(SUPPORTED_TYPE);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("rawtypes")
    @Override
    public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
            String repoType) { 

        	String content = cutSourceJson(sourceUrl);

    	
        int componentCount = 0;
        int licenseCount = 0;

        // According to tutorial https://github.com/FasterXML/jackson-databind/
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
        	List body = mapper.readValue(content, List.class);
        	for (int i = 0; i<body.size(); i++) {
                List<String> attributes = (List) body.get(i);       
                //Array contents: ["Name","Version","License","URL","VendorUrl","VendorName"]
                String name = (String) attributes.get(0);
                String version = (String) attributes.get(1);
                String repo = (String) attributes.get(3);
                String license = (String) attributes.get(2);
                String licenseUrl = repo;
                String homePage ="";
                
                //check whether VendorUrl is included in input file or not
                if(attributes.size()==6) {
                	homePage = (String) attributes.get(4);
                }else if (attributes.size()==5) {
                	homePage = repo;
                }

                ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
                appComponent.setApplication(application);
                componentCount++;      
                appComponent.setArtifactId(name);
                appComponent.setVersion(version);
                appComponent.setUsagePattern(usagePattern);
                appComponent.setGroupId("");
                appComponent.setOssHomepage(homePage);
                appComponent.setRepoType(repoType);

                addRawLicense(appComponent, license, licenseUrl, sourceUrl);            
            }
            doLogging(sourceUrl, application, componentCount, licenseCount);
        } catch (IOException e) {
            throw new SolicitorRuntimeException(
                    "Could not read yarn inventory source '" + sourceUrl + "'", e);
        }

    }

    
    //helper method that extracts information from the .json created by yarn licenses into a correct form
    private String cutSourceJson(String sourceURL) {
    	String filePath = sourceURL.replaceAll("file:", "");
    	File input = new File(filePath);
    	String content ="";
    	
    	try {
	    	BufferedReader reader = new BufferedReader(new FileReader(input));
	    	String line = "";
	    	

	    	//cuts last line of JSON 
	    	  while ((line = reader.readLine()) != null) 
	        {
	            content = line;
	        }
    	    
	    	
	    	//fixes URL issues
    		content = content.split("\\\"body\\\":")[1];
    		content = content.replace("}", "");
    		content = content.replace("git+", "");
    		content = content.replace("www.github", "github");
    		content = content.replace(".git", "");
    		content = content.replace("git://", "https://");
    		content = content.replace("git@github.com:", "https://github.com/");
    		content = content.replace("ssh://git@", "https://");
    		content = content.replace("Unknown", "");

	    	reader.close();

    	} catch (IOException e) {
    		
    	}
		return content;
    }

}
