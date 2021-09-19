/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.content.web;

import com.devonfw.tools.solicitor.common.content.ContentFactory;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public WebContent emptyContent() {

        return new WebContent(null);
    }

}
