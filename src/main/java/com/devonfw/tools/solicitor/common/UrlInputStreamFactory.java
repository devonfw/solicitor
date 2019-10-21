/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * An {@link InputStreamFactory} which creates {@link InputStream}s from
 * resources defined by their URL.
 *
 */
@Component
public class UrlInputStreamFactory implements InputStreamFactory {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     * 
     * Creates the {@link InputStream} from a resource given by the URL.
     * 
     * @see ApplicationContext#getResource(String)
     */
    @Override
    public InputStream createInputStreamFor(String url) throws IOException {

        return applicationContext.getResource(url).getInputStream();

    }

    /**
     * {@inheritDoc}
     * 
     * Checks that the referenced resource exists.
     */
    @Override
    public boolean isExisting(String url) {

        return applicationContext.getResource(url).exists();
    }

}
