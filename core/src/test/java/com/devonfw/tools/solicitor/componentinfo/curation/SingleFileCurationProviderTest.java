package com.devonfw.tools.solicitor.componentinfo.curation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.SelectorCurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;

/**
 * Test for {@link SingleFileCurationProvider}.
 *
 */
class SingleFileCurationProviderTest {

  private SingleFileCurationProvider objectUnderTest;

  @BeforeEach
  public void setup() {

    AllKindsPackageURLHandler packageUrlHandler = Mockito.mock(AllKindsPackageURLHandler.class);
    Mockito.when(packageUrlHandler.pathFor("pkg:maven/somenamespace/somecomponent@2.3.4"))
        .thenReturn("pkg/maven/somenamespace/somecomponent/2.3.4");
    Mockito.when(packageUrlHandler.pathFor("pkg:maven/somenamespace/somecomponent@2.3.5"))
        .thenReturn("pkg/maven/somenamespace/somecomponent/2.3.5");
    Mockito.when(packageUrlHandler.pathFor("pkg:maven/othernamespace/othercomponent@1.2.3"))
        .thenReturn("pkg/maven/othernamespace/othercomponent/1.2.3");

    this.objectUnderTest = new SingleFileCurationProvider(packageUrlHandler);
    this.objectUnderTest.setCurationsFileName("src/test/resources/curations/array_of_curations.yaml");
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider#findCurations(java.lang.String, java.lang.String)}.
   *
   * @throws ComponentInfoAdapterException
   * @throws CurationInvalidException
   */
  @Test
  void testFindCurationsWithSelectorNull() throws ComponentInfoAdapterException, CurationInvalidException {

    ComponentInfoCuration result;

    result = this.objectUnderTest.findCurations("pkg:maven/somenamespace/somecomponent@2.3.4",
        new SelectorCurationDataHandle(null));
    assertEquals("https://scancode-licensedb.aboutcode.org/apache-2.0.LICENSE", result.getLicenses().get(0).getUrl());
    result = this.objectUnderTest.findCurations("pkg:maven/somenamespace/somecomponent@2.3.5",
        new SelectorCurationDataHandle(null));
    assertEquals("https://scancode-licensedb.aboutcode.org/bsd-simplified.LICENSE",
        result.getLicenses().get(0).getUrl());
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider#findCurations(java.lang.String, java.lang.String)}.
   *
   * @throws ComponentInfoAdapterException
   * @throws CurationInvalidException
   */
  @Test
  void testFindCurationsWithSelectorNone() throws ComponentInfoAdapterException, CurationInvalidException {

    ComponentInfoCuration result;

    result = this.objectUnderTest.findCurations("pkg:maven/somenamespace/somecomponent@2.3.4",
        new SelectorCurationDataHandle("none"));
    assertNull(result);
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.SingleFileCurationProvider#findCurations(java.lang.String, java.lang.String)}.
   *
   * @throws ComponentInfoAdapterException
   * @throws CurationInvalidException
   */
  @Test
  void testFindCurationsUsingHierarchy() throws ComponentInfoAdapterException, CurationInvalidException {

    ComponentInfoCuration result;

    result = this.objectUnderTest.findCurations("pkg:maven/othernamespace/othercomponent@1.2.3",
        new SelectorCurationDataHandle(null));
    assertEquals("some/path/2", result.getLicenseCurations().get(0).getPath());
    assertEquals("some/path/1", result.getLicenseCurations().get(1).getPath());
  }

}
