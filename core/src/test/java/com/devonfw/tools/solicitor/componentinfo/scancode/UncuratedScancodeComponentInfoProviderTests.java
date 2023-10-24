package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.content.web.DirectUrlWebContentProvider;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider;

/**
 * This class contains JUnit test methods for the {@link UncuratedScancodeComponentInfoProvider} class.
 */
public class UncuratedScancodeComponentInfoProviderTests {

  // the object under test
  UncuratedScancodeComponentInfoProvider uncuratedScancodeComponentInfoProvider;

  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;
  
  SingleFileCurationProvider singleFileCurationProvider;
  
  @BeforeEach
  public void setup() {

    AllKindsPackageURLHandler packageURLHandler = Mockito.mock(AllKindsPackageURLHandler.class);

    Mockito.when(packageURLHandler.pathFor("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0"))
        .thenReturn("pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0");
    DirectUrlWebContentProvider contentProvider = new DirectUrlWebContentProvider(false);

    this.fileScancodeRawComponentInfoProvider = new FileScancodeRawComponentInfoProvider(contentProvider,
        packageURLHandler);
    this.fileScancodeRawComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");
    
    this.singleFileCurationProvider = new SingleFileCurationProvider(packageURLHandler);
    this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");
    
    this.uncuratedScancodeComponentInfoProvider = new UncuratedScancodeComponentInfoProvider(
        this.fileScancodeRawComponentInfoProvider, packageURLHandler, this.singleFileCurationProvider);
    this.uncuratedScancodeComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

  }
  
  /**
   * Test the {@link UncuratedScancodeComponentInfoProvider#getComponentInfo(String,String)} method when no curations file exists
   * 
   * @throws ComponentInfoAdapterException if something goes wrong
   */
    @Test
    public void testGetComponentInfoWithoutCurations() throws ComponentInfoAdapterException {
    	
      // given
      this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/nonexisting.yaml");
      
      // when
    	ScancodeComponentInfo scancodeComponentInfo = 
    			this.uncuratedScancodeComponentInfoProvider.getComponentInfo(
    					"pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", "someCurationSelector");
    			
      // then
      assertNotNull(scancodeComponentInfo);
      assertEquals("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", scancodeComponentInfo.getPackageUrl());   
      assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
      		scancodeComponentInfo.getNoticeFileContent());
      assertEquals(1, scancodeComponentInfo.getCopyrights().size());
      assertEquals("Copyright 2023 devonfw", scancodeComponentInfo.getCopyrights().toArray()[0]);
    }


    /**
     * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when the /src directory is excluded
     * 
     * @throws ComponentInfoAdapterException if something goes wrong
     */
    @Test
    public void testGetComponentInfoWithCurationsAndExclusions() throws ComponentInfoAdapterException {
    	
      // given
      this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations_with_exclusions.yaml");
      // when
     	ScancodeComponentInfo scancodeComponentInfo = 
     			this.uncuratedScancodeComponentInfoProvider.getComponentInfo(
     					"pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", "someCurationSelector");
     			
       // then
      assertNotNull(scancodeComponentInfo);
      assertEquals("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", scancodeComponentInfo.getPackageUrl());   
      assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
      		scancodeComponentInfo.getNoticeFileContent());
      assertEquals(0, scancodeComponentInfo.getCopyrights().size()); //since the copyright is found under /src/../SampleClass.java1, it will be excluded
    }
    /**
     * Test the {@link ScancodeComponentInfoAdapter#getComponentInfo(String,String)} method when curations exist but no 
     * paths are excluded
     * @throws ComponentInfoAdapterException if something goes wrong
     */
    @Test
    public void testGetComponentInfoWithCurationsAndWithoutExclusions() throws ComponentInfoAdapterException {
    	
      // given
      this.singleFileCurationProvider.setCurationsFileName("src/test/resources/scancodefileadapter/curations.yaml");
      
      // when
    	ScancodeComponentInfo scancodeComponentInfo = 
    			this.uncuratedScancodeComponentInfoProvider.getComponentInfo(
    					"pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", "someCurationSelector");
    			
      // then
      assertNotNull(scancodeComponentInfo);
      assertEquals("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0", scancodeComponentInfo.getPackageUrl());   
      assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.",
      		scancodeComponentInfo.getNoticeFileContent());
      assertEquals(1, scancodeComponentInfo.getCopyrights().size());
      assertEquals("Copyright 2023 devonfw", scancodeComponentInfo.getCopyrights().toArray()[0]); //The copyright curation does not apply on the scancodeComponentInfo object.
    }
}
