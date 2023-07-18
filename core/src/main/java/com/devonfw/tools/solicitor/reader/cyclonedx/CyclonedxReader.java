package com.devonfw.tools.solicitor.reader.cyclonedx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import com.devonfw.tools.solicitor.reader.AbstractReader;
import com.devonfw.tools.solicitor.reader.Reader;

import org.springframework.stereotype.Component;
import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Bom;
import org.cyclonedx.parsers.JsonParser;

@Component
public class CyclonedxReader extends AbstractReader implements Reader {
	/**
	 * The supported type of this {@link Reader}.
	 */
	public static final String SUPPORTED_TYPE = "cyclonedx";
	
	/** {@inheritDoc} */
	@Override
	public Set<String> getSupportedTypes() {
		return Collections.singleton(SUPPORTED_TYPE);
	}

	@Override
	public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
			String repoType, Map<String, String> configuration) {
			
	    int components = 0;
	    int licenses = 0;
	    InputStream is;

	    try {
	        is = this.inputStreamFactory.createInputStreamFor(sourceUrl);
	      } catch (IOException e1) {
	        throw new SolicitorRuntimeException("Could not open inventory source '" + sourceUrl + "' for reading", e1);
	      }
	    
	    // Create a JSON parser instance
	    JsonParser parser = new JsonParser();

	    try {
	   	// Parse the sbom.json file into a Bom object
		Bom bom = parser.parse(is);
		
        // Access the list of components in the Bom
        for (org.cyclonedx.model.Component component : bom.getComponents()) {
            ApplicationComponent appComponent = getModelFactory().newApplicationComponent();
            appComponent.setApplication(application);
            appComponent.setGroupId(component.getGroup());
            appComponent.setArtifactId(component.getName());
            appComponent.setVersion(component.getVersion());
            appComponent.setUsagePattern(usagePattern);
            appComponent.setRepoType(repoType);
            appComponent.setPackageUrl(component.getPurl());			// todo: check if this is the needed format
            components++;
            
            System.out.println(component.getGroup() + "     " + component.getName() + "    " + component.getVersion() );
            System.out.println(component.getPurl());
            if (component.getLicenseChoice().equals(null)) {
                // in case no license is found insert an empty entry
                addRawLicense(appComponent, null, null, sourceUrl);
              } else {
            	//Declared License can be written either in "name" or "id" field
                for (org.cyclonedx.model.License lic : component.getLicenseChoice().getLicenses()) {
                	if(lic.getName()!=null) {
                    addRawLicense(appComponent, lic.getName(), lic.getUrl(), sourceUrl);
                	}
                	else if (lic.getId()!=null) {
                        addRawLicense(appComponent, lic.getId(), lic.getUrl(), sourceUrl);
                	}
                  }
              }
        }
	    }
        catch (ParseException e) {
			e.printStackTrace();
		}
	    doLogging(sourceUrl, application, components, licenses);
	}



}
