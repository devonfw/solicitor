package com.devonfw.tools.solicitor.componentinfo.curation;

import com.devonfw.tools.solicitor.componentinfo.ComponentInfo;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoProvider;

/**
 * A {@link ComponentInfoProvider} which provides filtered {@link ComponentInfo}s. This is {@link ComponentInfo} which
 * is not yet fully curated but data is already filtered to remove information which applies to portions of the original
 * scanned code which should be disregarded.
 *
 */
public interface FilteredComponentInfoProvider extends ComponentInfoProvider {

}