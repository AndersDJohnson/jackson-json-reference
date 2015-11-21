package me.andrz.jackson;

/**
 * Generic exception for the package.
 */
public class JsonReferenceException extends Exception {

    public JsonReferenceException() {}

    public JsonReferenceException(String message) {
        super(message);
    }

    public JsonReferenceException(Throwable cause) {
        super(cause);
    }

    public JsonReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
