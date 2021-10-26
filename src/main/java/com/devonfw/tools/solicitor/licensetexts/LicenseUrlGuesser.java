/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import com.devonfw.tools.solicitor.common.content.ContentProvider;

/**
 * A {@link ContentProvider} which guesses an alternative license url for a given license url.
 */
public interface LicenseUrlGuesser extends ContentProvider<GuessedLicenseUrlContent> {

}
