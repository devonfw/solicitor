/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

public class SolicitorRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SolicitorRuntimeException() {

        super();
    }

    public SolicitorRuntimeException(String arg0, Throwable arg1) {

        super(arg0, arg1);
    }

    public SolicitorRuntimeException(String message) {

        super(message);
    }

    public SolicitorRuntimeException(Throwable cause) {

        super(cause);
    }

}
