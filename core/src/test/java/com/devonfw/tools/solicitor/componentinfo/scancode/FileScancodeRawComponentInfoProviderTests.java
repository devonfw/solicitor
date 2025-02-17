package com.devonfw.tools.solicitor.componentinfo.scancode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;

/**
 * This class contains JUnit test methods for the {@link FileScancodeRawComponentInfoProvider} class.
 */
public class FileScancodeRawComponentInfoProviderTests {

  // the object under test
  FileScancodeRawComponentInfoProvider fileScancodeRawComponentInfoProvider;

  @BeforeEach
  public void setup() throws SolicitorMalformedPackageURLException {

    AllKindsPackageURLHandler packageURLHandler = Mockito.mock(AllKindsPackageURLHandler.class);

    Mockito
        .when(packageURLHandler.pathFor(
            PackageURLHelper.fromString("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0")))
        .thenReturn("pkg/maven/com/devonfw/tools/test-project-for-deep-license-scan/0.1.0");

    this.fileScancodeRawComponentInfoProvider = new FileScancodeRawComponentInfoProvider(packageURLHandler);
    this.fileScancodeRawComponentInfoProvider.setRepoBasePath("src/test/resources/scancodefileadapter/Source/repo");

  }

  /**
   * Test the {@link FileScancodeRawComponentInfoProvider#retrieveContent(String, String)} method for a file which does
   * not exceed the given default size limit (which should be 1 Mio Bytes).
   *
   * @throws SolicitorMalformedPackageURLException
   */
  @Test
  public void testRetrieveContentFileSizeOkForDefault() throws SolicitorMalformedPackageURLException {

    // when
    String fileContent = this.fileScancodeRawComponentInfoProvider.retrieveContent(
        PackageURLHelper.fromString("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0"),
        "pkgcontent:/NOTICE.txt");

    // then
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.", fileContent);
  }

  /**
   * Test the {@link FileScancodeRawComponentInfoProvider#retrieveContent(String, String)} method for a file which does
   * not exceed the given size limit.
   *
   * @throws SolicitorMalformedPackageURLException
   */
  @Test
  public void testRetrieveContentFileSizeOk() throws SolicitorMalformedPackageURLException {

    // given
    this.fileScancodeRawComponentInfoProvider.setMaxContentFileSize(66);

    // when
    String fileContent = this.fileScancodeRawComponentInfoProvider.retrieveContent(
        PackageURLHelper.fromString("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0"),
        "pkgcontent:/NOTICE.txt");

    // then
    assertEquals("This is a dummy notice file for testing. Code is under Apache-2.0.", fileContent);
  }

  /**
   * Test the {@link FileScancodeRawComponentInfoProvider#retrieveContent(String, String)} method for a file which does
   * exceed the given size limit.
   *
   * @throws SolicitorMalformedPackageURLException
   */
  @Test
  public void testRetrieveContentFileSizeNotOk() throws SolicitorMalformedPackageURLException {

    // given
    this.fileScancodeRawComponentInfoProvider.setMaxContentFileSize(65);

    // when
    String fileContent = this.fileScancodeRawComponentInfoProvider.retrieveContent(
        PackageURLHelper.fromString("pkg:maven/com.devonfw.tools/test-project-for-deep-license-scan@0.1.0"),
        "pkgcontent:/NOTICE.txt");

    // then
    assertNull(fileContent);
  }

}
