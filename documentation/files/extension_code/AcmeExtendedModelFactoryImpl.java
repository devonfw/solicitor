package com.acme.compliance;

import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;

public class AcmeExtendedModelFactoryImpl extends ModelFactoryImpl {

  public AcmeExtendedModelFactoryImpl() {
    super();
    // the extended AcmeExtendedNormalizedLicenseImpl model class replaces the
    // original one and thus is registered with the same table name
    registerModelClass(AcmeExtendedNormalizedLicenseImpl.class, "NORMALIZEDLICENSE");
  }

  // override the factory method for NormalizedLicense
  @Override
  public AcmeExtendedNormalizedLicenseImpl newNormalizedLicense() {
    AcmeExtendedNormalizedLicenseImpl result = new AcmeExtendedNormalizedLicenseImpl();
    result.setLicenseContentProvider(getLicenseContentProvider());
    return result;
  }

  // override the factory method for NormalizedLicense
  @Override
  public AcmeExtendedNormalizedLicenseImpl newNormalizedLicense(RawLicense rawLicense) {
    AcmeExtendedNormalizedLicenseImpl result 
      = new AcmeExtendedNormalizedLicenseImpl(rawLicense);
    result.setLicenseContentProvider(getLicenseContentProvider());
    return result;
  }

}
