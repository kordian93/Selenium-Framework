package support.exceptions;

import org.openqa.selenium.InvalidElementStateException;

public class ElementNotPresentException extends InvalidElementStateException {

    public ElementNotPresentException() {
        super();
    }

    public ElementNotPresentException(String message) {
        super(message);
    }

    public ElementNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
