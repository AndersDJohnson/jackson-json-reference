package me.andrz.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class JsonRef {

    public static final Pattern pattern = Pattern.compile("([^\\#]*)\\#?(.*)");

    private String uri;
    private String pointer;

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
    public String toString() {
        return this.uri + "#" + this.pointer;
    }

}
