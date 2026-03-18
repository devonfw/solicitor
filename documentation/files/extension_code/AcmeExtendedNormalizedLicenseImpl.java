package com.acme.compliance;

import com.devonfw.tools.solicitor.model.impl.inventory.NormalizedLicenseImpl;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Extended implementation of the NormalizedLicense model class
 */
public class AcmeExtendedNormalizedLicenseImpl 
  extends NormalizedLicenseImpl
  implements AcmeExtendedNormalizedLicense {

  private String rating;

  // Redefine the existing constructors
  public AcmeExtendedNormalizedLicenseImpl() {
    super();
  }

  public AcmeExtendedNormalizedLicenseImpl(RawLicense rawLicense) {
    super(rawLicense);
  }

  @Override
  public String getRating() {
    return this.rating;
  }

  @Override
  public void setRating(String rating) {
    this.rating = rating;
  }

  // include the rating in the array returned by getDataElements
  @Override
  public String[] getDataElements() {
    return concatRow(super.getDataElements(), new String[] { this.rating });
  }

  // include "rating" in the array of head elements
  @Override
  public String[] getHeadElements() {
    return concatRow(super.getHeadElements(), new String[] { "rating" });
  }

  // enable reading the new property from json file
  @Override
  public void readNormalizedLicenseFromJsonNode(
                JsonNode normalizedLicenseNode, int readModelVersion) {
    super.readNormalizedLicenseFromJsonNode(normalizedLicenseNode, readModelVersion);
    if (normalizedLicenseNode.has("rating")) {
      this.rating = normalizedLicenseNode.get("rating").asText(null);
    }
  }
}