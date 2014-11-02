package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonPointer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents a JSON reference string (its URI and pointer).
 */
public class JsonRef {

    private URI uri;
    private JsonPointer pointer;
    private boolean local;
    private boolean absolute;

    public JsonRef() {}

    public JsonRef(URI uri) {

        String fragment = uri.getFragment();

        JsonPointer pointer;

        if (fragment == null || fragment.isEmpty()) {
            pointer = JsonPointer.compile(null);
        }
        else {
            pointer = JsonPointer.compile(fragment);
        }

        /*
         * Remove any extraneous path segments, especially to make semantically empty paths really empty.
         */
        uri = uri.normalize();

        absolute = uri.isAbsolute();
        local = ! uri.isAbsolute() && "".equals(uri.getPath());

        this.uri = uri;
        this.pointer = pointer;
    }

    public static JsonRef fromString(String string) throws JsonReferenceException {
        try {
            return fromURI(new URI(string));
        } catch (URISyntaxException e) {
            throw new JsonReferenceException("Invalid URI: " + string);
        }
    }

    public static JsonRef fromURI(URI uri) {
        return new JsonRef(uri);
    }

    public boolean isLocal() {
        return local;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public URI getUri() {
        return uri;
    }

    public JsonPointer getPointer() {
        return pointer;
    }

    public String toString() {
        return uri.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonRef jsonRef = (JsonRef) o;

        if (uri != null ? !uri.equals(jsonRef.uri) : jsonRef.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Override
    public JsonRef clone() {
        JsonRef ref = new JsonRef(uri);
        return ref;
    }

}
