package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonPointer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Represents a JSON reference string (its URI and pointer).
 */
public class JsonReference {

    private URI uri;
    private JsonPointer pointer;
    private boolean local;
    private boolean absolute;

    public JsonReference() {}

    private JsonReference(URI uri) {

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
        
        String path = uri.getPath();

        absolute = uri.isAbsolute();
        local = !absolute && (path == null || path.isEmpty());

        this.uri = uri;
        this.pointer = pointer;
    }

    public static JsonReference fromString(String string) throws JsonReferenceException {
        try {
            return fromURI(new URI(string));
        } catch (URISyntaxException e) {
            throw new JsonReferenceException("Invalid URI: " + string);
        }
    }

    public static JsonReference fromURI(URI uri) {
        return new JsonReference(uri);
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

        JsonReference jsonRef = (JsonReference) o;

        if (uri != null ? !uri.equals(jsonRef.uri) : jsonRef.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

}
