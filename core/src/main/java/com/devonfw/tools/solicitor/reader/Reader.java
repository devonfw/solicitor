/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader;

import java.util.Map;

import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

/**
 * {@link Reader}s read data about {@link ApplicationComponent}s and their associated {@link RawLicense}s from a
 * technology specific input file and thereby populate the Solicitor datamodel with raw data which can then be further
 * processed by the rule engine.
 *
 */
public interface Reader {

  /**
   * Indicates if the given {@link Reader} instance is capable of processing the given input type.
   *
   * @param type s string indicating of which type the input is
   * @return <code>true</code> if this {@link Reader} is capable of processing the type of data; <code>false</code>
   *         otherwise
   */
  public boolean accept(String type);

  /**
   * Read the inventory data contained in the given resource.
   *
   * @param type the input type, same as in {@link #accept(String)}.
   * @param sourceUrl a URL of the resource to read the data from
   * @param application all read {@link ApplicationComponent} need to be linked with this {@link Application}
   * @param usagePattern the {@link UsagePattern} which applies for all read {@link ApplicationComponent}s
   * @param repoType the type of Repository to download the sources from
   * @param packageType the packageType to create the packageUrl
   * @param configuration optional configuration settings for readers
   */
  public void readInventory(String type, String sourceUrl, Application application, UsagePattern usagePattern,
      String repoType, String packageType, Map<String, String> configuration);

}
