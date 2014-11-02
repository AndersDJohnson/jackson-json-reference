package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Wraps a JSON node with ability to resolve JSON references.
 */
public class RefResolvingJsonNode {

    private JsonNode node;

    public RefResolvingJsonNode(JsonNode node) {
        this.node = node;
    }

    public JsonNode get(JsonRef ref) {
        JsonNode referencedNode;

        String refPointer = ref.getPointer();

        JsonPointer jsonPointer = JsonPointer.compile(refPointer);

        referencedNode = node.at(jsonPointer);

        return referencedNode;
    }

    public JsonNode get(String refString) {
        refString = localize(refString);
        JsonRef ref = new JsonRef(refString);
        return get(ref);
    }

    public String localize(String refString) {
        if (! refString.startsWith("#")) {
            refString = '#' + refString;
        }
        return refString;
    }
}
