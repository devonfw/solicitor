/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.reader;

import com.capgemini.solicitor.model.masterdata.Application;
import com.capgemini.solicitor.model.masterdata.UsagePattern;

public interface Reader {

    public boolean accept(String type);

    public void readInventory(String sourceUrl, Application application,
            UsagePattern usagePattern);

}
