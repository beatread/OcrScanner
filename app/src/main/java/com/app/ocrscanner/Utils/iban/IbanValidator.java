package com.app.ocrscanner.Utils.iban;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class IbanValidator {

    private final Map<String, Validator> formatValidators;

    /**
     * The validation class
     */
    public static class Validator {
        private static final int MIN_LEN = 8;
        private static final int MAX_LEN = 34; // defined by [3]
        final String countryCode;
        final Pattern pattern;
        final int lengthOfIBAN; // used to avoid unnecessary regex matching

        /**
         * Creates the pattern
         *
         * @param cc     the country code
         * @param len    the length of the IBAN
         * @param format the regex to use to check the format
         */
        Validator(String cc, int len, String format) {
            if (!(cc.length() == 2 && Character.isUpperCase(cc.charAt(0)) && Character.isUpperCase(cc.charAt(1)))) {
                throw new IllegalArgumentException("Invalid country Code; must be exactly 2 upper-case characters");
            }
            if (len > MAX_LEN || len < MIN_LEN) {
                throw new IllegalArgumentException("Invalid length parameter, must be in range " + MIN_LEN + " to " + MAX_LEN + " inclusive: " + len);
            }
            if (!format.startsWith(cc)) {
                throw new IllegalArgumentException("countryCode '" + cc + "' does not agree with format: " + format);
            }
            this.countryCode = cc;
            this.lengthOfIBAN = len;
            this.pattern = Pattern.compile(format);
        }
    }

    private static final Validator[] DEFAULT_FORMATS = {
            new Validator("AD", 24, "AD\\d{10}[A-Z0-9]{12}"), // Andorra
            new Validator("AE", 23, "AE\\d{21}"), // United Arab Emirates
            new Validator("AL", 28, "AL\\d{10}[A-Z0-9]{16}"), // Albania
            new Validator("AT", 20, "AT\\d{18}"), // Austria
            new Validator("AZ", 28, "AZ\\d{2}[A-Z]{4}[A-Z0-9]{20}"), // Republic of Azerbaijan
            new Validator("BA", 20, "BA\\d{18}"), // Bosnia and Herzegovina
            new Validator("BE", 16, "BE\\d{14}"), // Belgium
            new Validator("BG", 22, "BG\\d{2}[A-Z]{4}\\d{6}[A-Z0-9]{8}"), // Bulgaria
            new Validator("BH", 22, "BH\\d{2}[A-Z]{4}[A-Z0-9]{14}"), // Bahrain (Kingdom of)
            new Validator("BR", 29, "BR\\d{25}[A-Z]{1}[A-Z0-9]{1}"), // Brazil
            new Validator("BY", 28, "BY\\d{2}[A-Z0-9]{4}\\d{4}[A-Z0-9]{16}"), // Republic of Belarus
            new Validator("CH", 21, "CH\\d{7}[A-Z0-9]{12}"), // Switzerland
            new Validator("CR", 22, "CR\\d{20}"), // Costa Rica
            new Validator("CY", 28, "CY\\d{10}[A-Z0-9]{16}"), // Cyprus
            new Validator("CZ", 24, "CZ\\d{22}"), // Czech Republic
            new Validator("DE", 22, "DE\\d{20}"), // Germany
            new Validator("DK", 18, "DK\\d{16}"), // Denmark
            new Validator("DO", 28, "DO\\d{2}[A-Z0-9]{4}\\d{20}"), // Dominican Republic
            new Validator("EE", 20, "EE\\d{18}"), // Estonia
            new Validator("ES", 24, "ES\\d{22}"), // Spain
            new Validator("FI", 18, "FI\\d{16}"), // Finland
            new Validator("FO", 18, "FO\\d{16}"), // Denmark (Faroes)
            new Validator("FR", 27, "FR\\d{12}[A-Z0-9]{11}\\d{2}"), // France
            new Validator("GB", 22, "GB\\d{2}[A-Z]{4}\\d{14}"), // United Kingdom
            new Validator("GE", 22, "GE\\d{2}[A-Z]{2}\\d{16}"), // Georgia
            new Validator("GI", 23, "GI\\d{2}[A-Z]{4}[A-Z0-9]{15}"), // Gibraltar
            new Validator("GL", 18, "GL\\d{16}"), // Denmark (Greenland)
            new Validator("GR", 27, "GR\\d{9}[A-Z0-9]{16}"), // Greece
            new Validator("GT", 28, "GT\\d{2}[A-Z0-9]{24}"), // Guatemala
            new Validator("HR", 21, "HR\\d{19}"), // Croatia
            new Validator("HU", 28, "HU\\d{26}"), // Hungary
            new Validator("IE", 22, "IE\\d{2}[A-Z]{4}\\d{14}"), // Ireland
            new Validator("IL", 23, "IL\\d{21}"), // Israel
            new Validator("IS", 26, "IS\\d{24}"), // Iceland
            new Validator("IT", 27, "IT\\d{2}[A-Z]{1}\\d{10}[A-Z0-9]{12}"), // Italy
            new Validator("IQ", 23, "IQ\\d{2}[A-Z]{4}\\d{15}"), // Iraq
            new Validator("JO", 30, "JO\\d{2}[A-Z]{4}\\d{4}[A-Z0-9]{18}"), // Jordan
            new Validator("KW", 30, "KW\\d{2}[A-Z]{4}[A-Z0-9]{22}"), // Kuwait
            new Validator("KZ", 20, "KZ\\d{5}[A-Z0-9]{13}"), // Kazakhstan
            new Validator("LB", 28, "LB\\d{6}[A-Z0-9]{20}"), // Lebanon
            new Validator("LC", 32, "LC\\d{2}[A-Z]{4}[A-Z0-9]{24}"), // Saint Lucia
            new Validator("LI", 21, "LI\\d{7}[A-Z0-9]{12}"), // Liechtenstein (Principality of)
            new Validator("LT", 20, "LT\\d{18}"), // Lithuania
            new Validator("LU", 20, "LU\\d{5}[A-Z0-9]{13}"), // Luxembourg
            new Validator("LV", 21, "LV\\d{2}[A-Z]{4}[A-Z0-9]{13}"), // Latvia
            new Validator("MC", 27, "MC\\d{12}[A-Z0-9]{11}\\d{2}"), // Monaco
            new Validator("MD", 24, "MD\\d{2}[A-Z0-9]{20}"), // Moldova
            new Validator("ME", 22, "ME\\d{20}"), // Montenegro
            new Validator("MK", 19, "MK\\d{5}[A-Z0-9]{10}\\d{2}"), // Macedonia, Former Yugoslav Republic of
            new Validator("MR", 27, "MR\\d{25}"), // Mauritania
            new Validator("MT", 31, "MT\\d{2}[A-Z]{4}\\d{5}[A-Z0-9]{18}"), // Malta
            new Validator("MU", 30, "MU\\d{2}[A-Z]{4}\\d{19}[A-Z]{3}"), // Mauritius
            new Validator("NL", 18, "NL\\d{2}[A-Z]{4}\\d{10}"), // The Netherlands
            new Validator("NO", 15, "NO\\d{13}"), // Norway
            new Validator("PK", 24, "PK\\d{2}[A-Z]{4}[A-Z0-9]{16}"), // Pakistan
            new Validator("PL", 28, "PL\\d{26}"), // Poland
            new Validator("PS", 29, "PS\\d{2}[A-Z]{4}[A-Z0-9]{21}"), // Palestine, State of
            new Validator("PT", 25, "PT\\d{23}"), // Portugal
            new Validator("QA", 29, "QA\\d{2}[A-Z]{4}[A-Z0-9]{21}"), // Qatar
            new Validator("RO", 24, "RO\\d{2}[A-Z]{4}[A-Z0-9]{16}"), // Romania
            new Validator("RS", 22, "RS\\d{20}"), // Serbia
            new Validator("SA", 24, "SA\\d{4}[A-Z0-9]{18}"), // Saudi Arabia
            new Validator("SC", 31, "SC\\d{2}[A-Z]{4}\\d{20}[A-Z]{3}"), // Seychelles
            new Validator("SE", 24, "SE\\d{22}"), // Sweden
            new Validator("SI", 19, "SI\\d{17}"), // Slovenia
            new Validator("SK", 24, "SK\\d{22}"), // Slovak Republic
            new Validator("SM", 27, "SM\\d{2}[A-Z]{1}\\d{10}[A-Z0-9]{12}"), // San Marino
            new Validator("ST", 25, "ST\\d{23}"), // Sao Tome and Principe
            new Validator("TL", 23, "TL\\d{21}"), // Timor-Leste
            new Validator("TN", 24, "TN\\d{22}"), // Tunisia
            new Validator("TR", 26, "TR\\d{8}[A-Z0-9]{16}"), // Turkey
            new Validator("UA", 29, "UA\\d{8}[A-Z0-9]{19}"), // Ukraine
            new Validator("VG", 24, "VG\\d{2}[A-Z]{4}\\d{16}"), // Virgin Islands, British
            new Validator("XK", 20, "XK\\d{18}"), // Republic of Kosovo
    };

    /**
     * The singleton instance which uses the default formats
     */
    private static final IbanValidator DEFAULT_IBAN_VALIDATOR = new IbanValidator();

    /**
     * Return a singleton instance of the IBAN pattern using the default formats
     *
     * @return A singleton instance of the ISBN pattern
     */
    @SuppressWarnings("SameReturnValue")
    public static IbanValidator getInstance() {
        return DEFAULT_IBAN_VALIDATOR;
    }

    /**
     * Create a default IBAN pattern.
     */
    private IbanValidator() {
        this(DEFAULT_FORMATS);
    }

    /**
     * Create an IBAN pattern from the specified map of IBAN formats.
     *
     * @param formatMap map of IBAN formats
     */
    private IbanValidator(@SuppressWarnings("SameParameterValue") Validator[] formatMap) {
        this.formatValidators = createValidators(formatMap);
    }

    private Map<String, Validator> createValidators(Validator[] formatMap) {
        Map<String, Validator> m = new ConcurrentHashMap<>();
        for (Validator v : formatMap) {
            m.put(v.countryCode, v);
        }
        return m;
    }

    /**
     * Validate an IBAN Code
     *
     * @param code The value validation is being performed on
     * @return <code>true</code> if the value is valid
     */
    public boolean isValid(String code) {
        Validator formatValidator = getValidator(code);
        if (formatValidator == null || code.length() != formatValidator.lengthOfIBAN || !formatValidator.pattern.matcher(code).matches()) {
            return false;
        }
        return IbanCheckDigit.IBAN_CHECK_DIGIT.isValid(code);
    }

    /**
     * Does the class have the required pattern?
     *
     * @param code the code to check
     * @return true if there is a pattern
     */
    public boolean hasValidator(String code) {
        return getValidator(code) != null;
    }

    /**
     * Gets a copy of the default Validators.
     *
     * @return a copy of the default Validator array
     */
    public Validator[] getDefaultValidators() {
        return Arrays.copyOf(DEFAULT_FORMATS, DEFAULT_FORMATS.length);
    }

    /**
     * Get the Validator for a given IBAN
     *
     * @param code a string starting with the ISO country code (e.g. an IBAN)
     * @return the pattern or {@code null} if there is not one registered.
     */
    private Validator getValidator(String code) {
        if (code == null || code.length() < 2) { // ensure we can extract the code
            return null;
        }
        String key = code.substring(0, 2);
        return formatValidators.get(key);
    }

    /**
     * Installs a pattern.
     * Will replace any existing entry which has the same countryCode
     *
     * @param validator the instance to install.
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalStateException if an attempt is made to modify the singleton pattern
     */
    private Validator setValidator(Validator validator) {
        if (this == DEFAULT_IBAN_VALIDATOR) {
            throw new IllegalStateException("The singleton pattern cannot be modified");
        }
        return formatValidators.put(validator.countryCode, validator);
    }

    /**
     * Installs a pattern.
     * Will replace any existing entry which has the same countryCode.
     *
     * @param countryCode the country code
     * @param length      the length of the IBAN. Must be ≥ 8 and ≤ 32.
     *                    If the length is < 0, the pattern is removed, and the format is not used.
     * @param format      the format of the IBAN (as a regular expression)
     * @return the previous Validator, or {@code null} if there was none
     * @throws IllegalArgumentException if there is a problem
     * @throws IllegalStateException    if an attempt is made to modify the singleton pattern
     */
    public Validator setValidator(String countryCode, int length, String format) {
        if (this == DEFAULT_IBAN_VALIDATOR) {
            throw new IllegalStateException("The singleton pattern cannot be modified");
        }
        if (length < 0) {
            return formatValidators.remove(countryCode);
        }
        return setValidator(new Validator(countryCode, length, format));
    }

}
