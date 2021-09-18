/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

/**
 * A {@link ContentFactory} for {@link WebContent}.
 */
public class WebContentFactory implements ContentFactory<WebContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public WebContent fromString(String string) {

        return new WebContent(string);
    }

}
