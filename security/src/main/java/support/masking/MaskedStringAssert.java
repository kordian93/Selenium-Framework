package support.masking;

import static org.testng.Assert.fail;
import static org.testng.internal.EclipseInterface.*;

public class MaskedStringAssert {

    public static void maskedAssertEquals(MaskedString actual, String expected, String message) {
        boolean equal = areEqual(actual, expected);
        if (!equal) {
            failNotEquals(actual, expected, message);
        }
    }

    public static void maskedAssertEquals(MaskedString actual, String expected) {
        maskedAssertEquals(actual, expected, null);
    }

    public static void maskedAssertEquals(String actual, MaskedString expected, String message) {
        boolean equal = areEqual(expected, actual);
        if (!equal) {
            failNotEquals(actual, expected, message);
        }
    }

    public static void maskedAssertEquals(String actual, MaskedString expected) {
        maskedAssertEquals(actual, expected, null);
    }

    private static boolean areEqual(MaskedString maskedString, String string) {
        if (string == null && maskedString == null) {
            return true;
        }
        // Only one of them is null
        if (string == null || maskedString == null) {
            return false;
        }
        return string.equals(maskedString.get()) && maskedString.equals(string);
    }

    private static void failNotEquals(String actual, MaskedString expected, String message) {
        fail(format(actual, expected, message));
    }

    private static void failNotEquals(MaskedString actual, String expected, String message) {
        fail(format(actual, expected, message));
    }

    static String format(Object actual, Object expected, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }
        return formatted + ASSERT_EQUAL_LEFT + expected + ASSERT_MIDDLE +
                "Not logged to ensure confidentiality. Try running the test in debug mode" + ASSERT_RIGHT;
    }
}
