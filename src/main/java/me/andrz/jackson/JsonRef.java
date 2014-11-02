package me.andrz.jackson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a JSON reference string (its URI and pointer).
 */
public class JsonRef {

    public static final Pattern pattern = Pattern.compile("([^\\#]*)\\#?(.*)");

    private String uri;
    private String pointer;

    public JsonRef() {}

    public JsonRef(String ref) {
        this.parseReference(ref);
    }

    public void parseReference(String ref) {
        String uri;
        String pointer = null;

        Matcher matcher = pattern.matcher(ref);
        matcher.find();
        uri = matcher.group(1);
        int groupCount = matcher.groupCount();
        if (groupCount > 1) {
            pointer = matcher.group(2);
        }
        this.uri = uri;
        this.pointer = pointer;
    }

    public boolean isForLocal() {
        return "".equals(uri);
    }

    public boolean isForAbsoluteUrl() {
        return uri.matches("^(.*?)://.*");
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonRef jsonRef = (JsonRef) o;

        if (pointer != null ? !pointer.equals(jsonRef.pointer) : jsonRef.pointer != null) return false;
        if (uri != null ? !uri.equals(jsonRef.uri) : jsonRef.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (pointer != null ? pointer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.uri + "#" + this.pointer;
    }

    @Override
    public JsonRef clone() {
        JsonRef ref = new JsonRef();
        ref.setUri(uri);
        ref.setPointer(pointer);
        return ref;
    }

}
