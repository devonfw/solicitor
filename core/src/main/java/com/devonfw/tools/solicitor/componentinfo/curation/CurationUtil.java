package com.devonfw.tools.solicitor.componentinfo.curation;

import java.util.ArrayList;
import java.util.List;

import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Helper methods for working with curation data.
 */
public class CurationUtil {

  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  /**
   * Parses curation data from a YAML string into a ComponentInfoCuration object.
   *
   * @param curationYaml The YAML data to be parsed.
   * @return A ComponentInfoCuration object containing parsed curation data.
   * @throws CurationInvalidException If any errors occur during YAML parsing.
   */
  public static ComponentInfoCuration curationDataFromString(String curationYaml) throws CurationInvalidException {

    ComponentInfoCuration result;
    try {
      // Use Jackson YAML mapper to parse YAML into an object
      result = yamlMapper.readValue(curationYaml, ComponentInfoCuration.class);
    } catch (JsonProcessingException e) {
      throw new CurationInvalidException(e);
    }
    if (result.getName() == null || result.getName().isEmpty()) {
      throw new CurationInvalidException("The name attribute of the curation data must not be null or empty");
    }
    return result;
  }

  /**
   * Private constructor prohibits instantiation
   */
  private CurationUtil() {

  }

  /**
   * Merges two ComponentInfoCuration objects. The "second" argument represents the more specific one (with higher
   * priority): name and url of the second one supersede the first one. If data is concatenated as list, then the
   * elements from the second are are front of the list, the elements from the first are at the back.
   *
   * @param first the first object (less spoecific / lower priority)
   * @param second the second object (more specific / higher priority)
   * @return the merged result
   */
  @SuppressWarnings("unchecked")
  public static ComponentInfoCuration merge(ComponentInfoCuration first, ComponentInfoCuration second) {

    if (first == null) {
      return second;
    }
    if (second == null) {
      return first;
    }
    ComponentInfoCuration merged = new ComponentInfoCuration();
    merged.setName(second.getName() != null ? second.getName() : first.getName()); // latest wins
    merged.setNote(join(" / ", second.getNote(), first.getNote()));
    merged.setUrl(second.getUrl() != null ? second.getUrl() : first.getUrl()); // latest wins
    merged.setCopyrights(join(second.getCopyrights(), first.getCopyrights()));
    merged.setLicenses(join(second.getLicenses(), first.getLicenses()));
    merged.setExcludedPaths(join(second.getExcludedPaths(), first.getExcludedPaths()));
    merged.setLicenseCurations(join(second.getLicenseCurations(), first.getLicenseCurations()));
    merged.setCopyrightCurations(join(second.getCopyrightCurations(), first.getCopyrightCurations()));
    return merged;
  }

  private static String join(String delimiter, String arg1, String arg2) {

    if (arg1 == null && arg2 == null) {
      return null;
    }
    if (arg1 != null) {
      StringBuffer sb = new StringBuffer(arg1);
      if (arg2 != null) {
        sb.append(delimiter).append(arg2);
      }
      return sb.toString();
    } else {
      return arg2;
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static List join(List arg1, List arg2) {

    if (arg1 == null && arg2 == null) {
      return null;
    }
    if (arg1 != null) {
      List result = new ArrayList(arg1);
      if (arg2 != null) {
        result.addAll(arg2);
      }
      return result;
    } else {
      return arg2;
    }
  }

}
