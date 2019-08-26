/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.common;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UrlInputStreamFactory implements InputStreamFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public InputStream createInputStreamFor(String url) throws IOException {

        return applicationContext.getResource(url).getInputStream();

    }

}
