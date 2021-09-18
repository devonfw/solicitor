/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.common.webcontent;

/**
 * TODO ohecker: This type ...
 *
 * @author <a href="TODO@sdm.de">TODO</a>
 * @version $Revision$
 */
public class WebContent implements Content {
    private String content;

    public WebContent(String content) {

        this.content = content;
    }

    public String getContent() {

        return this.content;
    }

}
