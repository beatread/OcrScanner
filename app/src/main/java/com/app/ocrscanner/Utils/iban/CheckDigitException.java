package com.app.ocrscanner.Utils.iban;

public class CheckDigitException extends Exception {

    private static final long serialVersionUID = -3519894732624685477L;

    /**
     * Construct an Exception with no message.
     */
    public CheckDigitException() {
    }

    /**
     * Construct an Exception with a message.
     *
     * @param msg The error message.
     */
    CheckDigitException(String msg) {
        super(msg);
    }

    /**
     * Construct an Exception with a message and
     * the underlying cause.
     *
     * @param msg   The error message.
     * @param cause The underlying cause of the error
     */
    public CheckDigitException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
