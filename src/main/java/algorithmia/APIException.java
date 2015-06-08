package algorithmia;

import java.io.IOException;

/**
 * APIException indicates a problem communicating with Algorithmia
 */
public class APIException extends IOException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new API exception with the specified detail message, no stack trace.
     * @param message the detail message
     */
    public APIException(String message) {
        super(message);
    }

    /**
     * Constructs a new API exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of the AlgorithmException
     */
    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

}
