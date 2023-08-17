package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapter;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;

/**
 * A {@link ComponentInfoAdapter} which takes uncurated {@link ComponentInfo} data from the configuret
 * {@link UncuratedComponentInfoProvider} and curates it via the given {@link ComponentInfoCurator}.
 *
 */
public class CuratingComponentInfoAdapter implements ComponentInfoAdapter {

  private UncuratedComponentInfoProvider uncuratedComponentInfoProvider;

  private ComponentInfoCurator componentInfoCurator;

  /**
   * The constructor.
   *
   * @param uncuratedComponentInfoProvider the provider of the uncurated {@link ComponentInfo} data
   * @param componentInfoCurator the curator to take
   */
  public CuratingComponentInfoAdapter(UncuratedComponentInfoProvider uncuratedComponentInfoProvider,
      ComponentInfoCurator componentInfoCurator) {

    this.uncuratedComponentInfoProvider = uncuratedComponentInfoProvider;
    this.componentInfoCurator = componentInfoCurator;
  }

  /**
   * Retrieves the component information and curations for a package identified by the given package URL. Returns the
   * data as a {@link ComponentInfo} object.
   *
   * @param packageUrl The identifier of the package for which information is requested
   * @return the data derived from the scancode results after applying any defined curations. <code>null</code> is
   *         returned if no data is available,
   * @throws ComponentInfoAdapterException if there was an exception when reading the data. In case that there is no
   *         data available no exception will be thrown. Instead <code>null</code> will be return in such a case.
   */
  @Override
  public ComponentInfo getComponentInfo(String packageUrl) throws ComponentInfoAdapterException {

    if (isFeatureActive()) {

      ComponentInfo componentInfo = this.uncuratedComponentInfoProvider.getComponentInfo(packageUrl);
      if (componentInfo == null) {
        return null;
      }
      componentInfo = this.componentInfoCurator.curate(componentInfo);

      return componentInfo;

    } else {
      return null;
    }

  }

  /**
   * Returns if the adapter should be active. Default implementation returns <code>true</code>. Subclasses might
   * override this to enable/disable this functionality depending on some configuration;
   *
   * @return <code>true</code> if the adapter shall be active, <code>false</code> otherwise.
   */
  protected boolean isFeatureActive() {

    return true;
  }

}