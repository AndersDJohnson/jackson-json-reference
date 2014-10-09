package me.andrz.jackson;

/**
 * Created by Anders on 10/8/2014.
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
