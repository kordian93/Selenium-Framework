package support.utils;

import lombok.extern.log4j.Log4j2;
import support.annotations.StringOrMasked;
import support.masking.MaskedString;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Pattern;

@Log4j2
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static boolean isValidEmail(@StringOrMasked Object value) {
        String stringValue = getStringValue(value);
        return stringValue != null && EMAIL_PATTERN.matcher(stringValue).matches();
    }

    public static boolean isZeroOrPositiveInteger(@StringOrMasked Object value) {
        if (isInteger(value)) {
            int intValue = (value instanceof MaskedString ms) ? ms.getAsInt() : Integer.parseInt((String) value);
            return intValue >= 0;
        } else {
            return false;
        }
    }

    public static boolean isInteger(@StringOrMasked Object value) {
        String stringValue = getStringValue(value);

        try {
            Integer.parseInt(stringValue);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isValidUrl(@StringOrMasked Object value) {
        String stringValue = getStringValue(value);

        try {
            URL url = URI.create(stringValue).toURL();

            int port = url.getPort();
            return port == -1 || (port >= 1 && port <= 65535);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static BigDecimal getPriceFromText(String str) {
        String stringWithComma = str.replaceAll("(?!\\d|,).", "");
        return new BigDecimal(stringWithComma.replace(",", "."));
    }

    private static String getStringValue(@StringOrMasked Object value) {
        return (value instanceof MaskedString ms) ? ms.get() : String.valueOf(value);
    }
}
