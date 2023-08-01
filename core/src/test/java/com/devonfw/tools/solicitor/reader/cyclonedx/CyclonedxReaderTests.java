
package com.devonfw.tools.solicitor.reader.cyclonedx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.FileInputStreamFactory;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLException;
import com.devonfw.tools.solicitor.common.packageurl.impl.DelegatingPackageURLHandlerImpl;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;
import org.mockito.Mockito;
/**
 * This class contains JUnit test methods for the {@link CyclonedxReader} class.
 */
public class CyclonedxReaderTests {
  private static final Logger LOG = LoggerFactory.getLogger(CyclonedxReaderTests.class);

  CyclonedxReader cdxr = new CyclonedxReader();			
  ModelFactory modelFactory = new ModelFactoryImpl();
  
  //Create Mock for DelegatingPackageURLHandlerImpl
  DelegatingPackageURLHandlerImpl delegatingPurlHandler = Mockito.mock(DelegatingPackageURLHandlerImpl.class);

  /**
   * Test the {@link CyclonedxReader#readInventory()} method.
   * Input file is an SBOM containing maven components.
   * Mock the case, that a maven PurlHandler exists.
   */
  @Test
  public void readMavenFileAndCheckSize() {
	  
    // Always return a non-empty String for maven purls
    Mockito.when(this.delegatingPurlHandler.sourceDownloadUrlFor(Mockito.startsWith("pkg:maven/"))).thenReturn("foo");

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.setDelegatingPackageURLHandler(delegatingPurlHandler);
    this.cdxr.readInventory("maven", "src/test/resources/mavensbom.json", application, UsagePattern.DYNAMIC_LINKING, "cyclonedx", null);
    LOG.info(application.toString());
    
    assertEquals(32, application.getApplicationComponents().size());				

    boolean found = false;
    for (ApplicationComponent ap : application.getApplicationComponents()) {
      if (ap.getGroupId().equals("org.springframework.boot") && //
          ap.getArtifactId().equals("spring-boot-starter-logging") && //
          ap.getVersion().equals("2.3.3.RELEASE")) {
        found = true;
        assertEquals("pkg:maven/org.springframework.boot/spring-boot-starter-logging@2.3.3.RELEASE?type=jar",
            ap.getPackageUrl());
        break;
      }
    }
    assertTrue(found);
  }
  /**
   * Test the {@link CyclonedxReader#readInventory()} method.
   * Input file is an SBOM containing maven components.
   * Mock the case, that a maven PurlHandler does not exist.
   */
  @Test
  public void readMavenFileAndCheckSizeNegative() {
	// Always throw exception for maven purls
    Mockito.when(this.delegatingPurlHandler.sourceDownloadUrlFor(Mockito.startsWith("pkg:maven/"))).thenThrow(new SolicitorPackageURLException(
            "No applicable SingleKindPackageURLHandler found for type maven"));

    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Java8");
    this.cdxr.setModelFactory(this.modelFactory);
    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
    this.cdxr.setDelegatingPackageURLHandler(delegatingPurlHandler);
    this.cdxr.readInventory("maven", "src/test/resources/mavensbom.json", application, UsagePattern.DYNAMIC_LINKING, "cyclonedx", null);
    LOG.info(application.toString());
 
    assertEquals(32, application.getApplicationComponents().size());				

  }
  
  /**
   * Test the {@link CyclonedxReader#readInventory()} method.
   * Input file is an SBOM containing npm components.
   * Mock the case, that a npm PurlHandler exists.
   */
  @Test
  public void readNpmFileAndCheckSize() {
		
	    // Always return a non-empty String for npm purls
	    Mockito.when(this.delegatingPurlHandler.sourceDownloadUrlFor(Mockito.startsWith("pkg:npm/"))).thenReturn("foo");

	    Application application = this.modelFactory.newApplication("testApp", "0.0.0.TEST", "1.1.2111", "http://bla.com", "Angular");
	    this.cdxr.setModelFactory(this.modelFactory);
	    this.cdxr.setInputStreamFactory(new FileInputStreamFactory());
	    this.cdxr.setDelegatingPackageURLHandler(delegatingPurlHandler);
	    this.cdxr.readInventory("npm", "src/test/resources/npmsbom.json", application, UsagePattern.DYNAMIC_LINKING, "cyclonedx", null);
	    LOG.info(application.toString());
	    
	    assertEquals(74, application.getApplicationComponents().size());				

	    boolean found = false;
	    for (ApplicationComponent ap : application.getApplicationComponents()) {
	      if (ap.getArtifactId().equals("async") && ap.getVersion().equals("3.2.4")) {
	        found = true;
	        assertEquals("pkg:npm/async@3.2.4", ap.getPackageUrl());
	        break;
	      }
	    }
	    assertTrue(found);
  }
}
