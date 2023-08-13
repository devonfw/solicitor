package com.devonfw.tools.solicitor.componentinfo.curating;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.componentinfo.curating.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curating.model.CurationList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * This class contains JUnit test methods for deserializing YAML encoded curation data.
 */
class CurationReadingTest {

  /**
   * Test reading a curation for a component which has all fields set.
   *
   * @throws IOException might be thown by the ObjectMapper.
   *
   */
  @Test
  public void testReadSingleCurationComplete() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();

    // when
    ComponentInfoCuration curation = mapper.readValue(
        new File("src/test/resources/curations/curation_with_all_fields_set.yaml"), ComponentInfoCuration.class);

    // then
    assertNotNull(curation);
    assertEquals("pkg/maven/somenamespace/somecomponent/2.3.4", curation.getName());
    assertEquals("some note on the curation", curation.getNote());
    assertEquals("http://the/sourceRepoUrl", curation.getUrl());
    assertEquals(3, curation.getLicenses().size());
    assertEquals("Apache-2.0", curation.getLicenses().get(0).getLicense());
    assertEquals("https://scancode-licensedb.aboutcode.org/apache-2.0.LICENSE", curation.getLicenses().get(0).getUrl());
    assertEquals(2, curation.getCopyrights().size());
    assertEquals("Copyright (c) 2003 First Holder", curation.getCopyrights().get(0));

  }

  /**
   * Test reading a curation for a component where some fields are not set.
   *
   * @throws IOException might be thown by the ObjectMapper.
   *
   */
  @Test
  public void testReadSingleCurationPartial() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();

    // when
    ComponentInfoCuration curation = mapper.readValue(
        new File("src/test/resources/curations/curation_with_fields_missing.yaml"), ComponentInfoCuration.class);

    // then
    assertNotNull(curation);
    assertEquals("pkg/maven/somenamespace/somecomponent/2.3.4", curation.getName());
    assertEquals("some note on the curation", curation.getNote());
    assertNull(curation.getUrl());
    assertNull(curation.getLicenses());
    assertEquals(0, curation.getCopyrights().size());

  }

  /**
   * Test reading an aarry of curation infos.
   *
   * @throws IOException might be thown by the ObjectMapper.
   *
   */
  @Test
  public void testReadListOfCurations() throws IOException {

    // given
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();

    // when
    CurationList curations = mapper.readValue(new File("src/test/resources/curations/array_of_curations.yaml"),
        CurationList.class);

    // then
    assertNotNull(curations);
    assertEquals(2, curations.getArtifacts().size());
    assertEquals("pkg/maven/somenamespace/somecomponent/2.3.4", curations.getArtifacts().get(0).getName());
    assertEquals("Apache-2.0", curations.getArtifacts().get(0).getLicenses().get(0).getLicense());
    assertEquals("pkg/maven/somenamespace/somecomponent/2.3.5", curations.getArtifacts().get(1).getName());
    assertEquals("BSD-2-Clause", curations.getArtifacts().get(1).getLicenses().get(0).getLicense());

  }
}
