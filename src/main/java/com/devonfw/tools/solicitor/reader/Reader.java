/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.reader;

import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.UsagePattern;

public interface Reader {

    public boolean accept(String type);

    public void readInventory(String sourceUrl, Application application,
            UsagePattern usagePattern);

}
