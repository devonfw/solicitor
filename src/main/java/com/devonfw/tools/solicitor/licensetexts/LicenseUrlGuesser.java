/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import com.devonfw.tools.solicitor.common.content.ContentProvider;

/**
 * A {@link ContentProvider} which guesses an alternative license url for a
 * given license url.
 */
public class LicenseUrlGuesser implements ContentProvider<GuessedLicenseUrlContent> {

    /**
     * {@inheritDoc}
     */
    @Override
    public GuessedLicenseUrlContent getContentForUri(String uri) {

        // TODO do the real stuff
        return new GuessedLicenseUrlContent("https://raw.githubusercontent.com/dom4j/dom4j/master/LICENSE",
                "some comment\nand some other comment");
    }

}
