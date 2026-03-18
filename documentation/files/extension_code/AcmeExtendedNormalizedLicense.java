package com.acme.compliance;

import com.devonfw.tools.solicitor.model.inventory.NormalizedLicense;

/**
 * Represents a NormalizedLicense with additional property "rating".
 */
public interface AcmeExtendedNormalizedLicense extends NormalizedLicense {

  String getRating();

  void setRating(String rating);

}
