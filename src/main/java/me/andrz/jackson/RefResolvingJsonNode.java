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

        JsonPointer pointer = ref.getPointer();

        referencedNode = node.at(pointer);

        return referencedNode;
    }

    public JsonNode get(String refString) throws JsonReferenceException {
        refString = localize(refString);
        JsonRef ref = JsonRef.fromString(refString);
        return get(ref);
    }

    public String localize(String refString) {
        if (! refString.startsWith("#")) {
            refString = '#' + refString;
        }
        return refString;
    }
}
