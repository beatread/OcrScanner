package com.app.ocrscanner.Utils.iban;

interface CheckDigit {

    /**
     * Calculates the <i>Check Digit</i> for a code.
     *
     * @param code The code to calculate the Check Digit for.
     *             The string must not include the check digit
     * @return The calculated Check Digit
     * @throws CheckDigitException if an error occurs.
     */
    String calculate(String code) throws CheckDigitException;

    /**
     * Validates the check digit for the code.
     *
     * @param code The code to validate, the string must include the check digit.
     * @return <code>true</code> if the check digit is valid, otherwise
     * <code>false</code>.
     */
    boolean isValid(String code);

}
