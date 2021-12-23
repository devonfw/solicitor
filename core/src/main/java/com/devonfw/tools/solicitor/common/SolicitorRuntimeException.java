/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

/**
 * Solicitor specific {@link java.lang.RuntimeException}. Exceptions of this
 * type will be thrown if processing needs to be aborted.
 */
public class SolicitorRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public SolicitorRuntimeException() {

        super();
    }

    /**
     * Constructor.
     *
     * @param message the message
     */
    public SolicitorRuntimeException(String message) {

        super(message);
    }

    /**
     * Constructor.
     *
     * @param message the message
     * @param cause underlying cause
     */
    public SolicitorRuntimeException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause the underlying cause
     */
    public SolicitorRuntimeException(Throwable cause) {

        super(cause);
    }

}
