package com.app.ocrscanner.Utils.iban;

import java.io.Serializable;

public class IbanCheckDigit implements CheckDigit, Serializable {

    private static final int MIN_CODE_LEN = 5;

    private static final long serialVersionUID = -3600191725934382801L;

    private static final int MAX_ALPHANUMERIC_VALUE = 35; // Character.getNumericValue('Z')

    /**
     * Singleton IBAN Number Check Digit instance
     */
    static final CheckDigit IBAN_CHECK_DIGIT = new IbanCheckDigit();

    private static final long MAX = 999999999;

    private static final long MODULUS = 97;

    /**
     * Construct Check Digit routine for IBAN Numbers.
     */
    private IbanCheckDigit() {
    }

    /**
     * Validate the check digit of an IBAN code.
     *
     * @param code The code to validate
     * @return <code>true</code> if the check digit is valid, otherwise
     * <code>false</code>
     */
    @Override
    public boolean isValid(String code) {
        if (code == null || code.length() < MIN_CODE_LEN) {
            return false;
        }
        String check = code.substring(2, 4); // CHECKSTYLE IGNORE MagicNumber
        if ("00".equals(check) || "01".equals(check) || "99".equals(check)) {
            return false;
        }
        try {
            int modulusResult = calculateModulus(code);
            return (modulusResult == 1);
        } catch (CheckDigitException ex) {
            return false;
        }
    }

    /**
     * Calculate the <i>Check Digit</i> for an IBAN code.
     * <p>
     * <b>Note:</b> The check digit is the third and fourth
     * characters and is set to the value "<code>00</code>".
     *
     * @param code The code to calculate the Check Digit for
     * @return The calculated Check Digit as 2 numeric decimal characters, e.g. "42"
     * @throws CheckDigitException if an error occurs calculating
     *                             the check digit for the specified code
     */
    @Override
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() < MIN_CODE_LEN) {
            throw new CheckDigitException("Invalid Code length=" +
                    (code == null ? 0 : code.length()));
        }
        code = code.substring(0, 2) + "00" + code.substring(4); // CHECKSTYLE IGNORE MagicNumber
        int modulusResult = calculateModulus(code);
        int charValue = (98 - modulusResult); // CHECKSTYLE IGNORE MagicNumber
        String checkDigit = Integer.toString(charValue);
        return (charValue > 9 ? checkDigit : "0" + checkDigit); // CHECKSTYLE IGNORE MagicNumber
    }

    /**
     * Calculate the modulus for a code.
     *
     * @param code The code to calculate the modulus for.
     * @return The modulus value
     * @throws CheckDigitException if an error occurs calculating the modulus
     *                             for the specified code
     */
    private int calculateModulus(String code) throws CheckDigitException {
        String reformattedCode = code.substring(4) + code.substring(0, 4); // CHECKSTYLE IGNORE MagicNumber
        long total = 0;
        for (int i = 0; i < reformattedCode.length(); i++) {
            int charValue = CharacterGetNumericValue.getNumericValue(reformattedCode.charAt(i));
            if (charValue < 0 || charValue > MAX_ALPHANUMERIC_VALUE) {
                throw new CheckDigitException("Invalid Character[" +
                        i + "] = '" + charValue + "'");
            }
            total = (charValue > 9 ? total * 100 : total * 10) + charValue; // CHECKSTYLE IGNORE MagicNumber
            if (total > MAX) {
                total = total % MODULUS;
            }
        }
        return (int) (total % MODULUS);
    }

}
