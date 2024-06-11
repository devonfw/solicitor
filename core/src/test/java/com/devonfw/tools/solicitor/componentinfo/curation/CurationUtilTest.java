package com.devonfw.tools.solicitor.componentinfo.curation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.common.IOHelper;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.LicenseInfoCuration;

/**
 * Test for {@link CurationUtil}.
 *
 */
class CurationUtilTest {

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.CurationUtil#curationDataFromString(java.lang.String)}.
   *
   * @throws CurationInvalidException
   * @throws IOException
   */
  @Test
  void testCurationDataFromString() throws CurationInvalidException, IOException {

    try (FileInputStream is = new FileInputStream("src/test/resources/curations/curations.txt")) {

      String testData = IOHelper.readStringFromInputStream(is);
      ComponentInfoCuration curations = CurationUtil.curationDataFromString(testData);
      assertEquals("some note", curations.getNote());
    }
  }

  /**
   * Test method for
   * {@link com.devonfw.tools.solicitor.componentinfo.curation.CurationUtil#curationDataFromString(java.lang.String)}.
   *
   * @throws CurationInvalidException
   * @throws IOException
   */
  @Test
  void testCurationDataFromStringMissingName() throws CurationInvalidException, IOException {

    assertThrows(CurationInvalidException.class, () -> CurationUtil.curationDataFromString("foo"));
  }

  /**
   * Test method for {@link CurationUtil#merge(ComponentInfoCuration, ComponentInfoCuration)}
   */
  @Test
  void testMerge() {

    ComponentInfoCuration curationFirst = new ComponentInfoCuration();
    curationFirst.setName("firstName");
    curationFirst.setNote("firstNote");
    curationFirst.setUrl("firstUrl");
    curationFirst.setExcludedPaths(Collections.singletonList("first path"));
    curationFirst.setCopyrights(Arrays.asList(new String[] { "first copyright 1", "first copyright 2" }));
    LicenseInfoCuration licFirst = new LicenseInfoCuration();
    curationFirst.setLicenses(Collections.singletonList(licFirst));
    CopyrightCuration ccFirst = new CopyrightCuration();
    curationFirst.setCopyrightCurations(Collections.singletonList(ccFirst));
    LicenseCuration lcFirst = new LicenseCuration();
    curationFirst.setLicenseCurations(Collections.singletonList(lcFirst));

    ComponentInfoCuration curationSecond = new ComponentInfoCuration();
    curationSecond.setName("secondName");
    curationSecond.setNote("secondNote");
    curationSecond.setUrl("secondUrl");
    curationSecond.setExcludedPaths(Collections.singletonList("second path"));
    curationSecond.setCopyrights(Arrays.asList(new String[] { "second copyright 1", "second copyright 2" }));
    LicenseInfoCuration licSecond = new LicenseInfoCuration();
    curationSecond.setLicenses(Collections.singletonList(licSecond));
    CopyrightCuration ccSecond = new CopyrightCuration();
    curationSecond.setCopyrightCurations(Collections.singletonList(ccSecond));
    LicenseCuration lcSecond = new LicenseCuration();
    curationSecond.setLicenseCurations(Collections.singletonList(lcSecond));

    ComponentInfoCuration merged = CurationUtil.merge(curationFirst, curationSecond);
    assertEquals("secondName", merged.getName());
    assertTrue(merged.getNote().startsWith("secondNote"));
    assertTrue(merged.getNote().contains("/"));
    assertTrue(merged.getNote().endsWith("firstNote"));
    assertEquals("secondUrl", merged.getUrl());
    assertTrue(merged.getCopyrights().equals(Arrays.asList(
        new String[] { "second copyright 1", "second copyright 2", "first copyright 1", "first copyright 2" })));
    assertTrue(merged.getLicenses().equals(Arrays.asList(new LicenseInfoCuration[] { licSecond, licFirst })));
    assertTrue(merged.getExcludedPaths().equals(Arrays.asList(new String[] { "second path", "first path" })));
    assertTrue(merged.getLicenseCurations().equals(Arrays.asList(new LicenseCuration[] { lcSecond, lcFirst })));
    assertTrue(merged.getCopyrightCurations().equals(Arrays.asList(new CopyrightCuration[] { ccSecond, ccFirst })));
  }
}
