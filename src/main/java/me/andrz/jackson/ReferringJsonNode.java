package me.andrz.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;

/**
 * Created by Anders on 11/1/2014.
 */
public class ReferringJsonNode {

    private JsonNode node;

    public ReferringJsonNode(JsonNode node) {
        this.node = node;
    }

    public JsonNode get(JsonRef ref) throws JsonPointerException {
        JsonNode referencedNode;

        String refPointer = ref.getPointer();

        JsonPointer jsonPointer = new JsonPointer(refPointer);
        referencedNode = jsonPointer.get(node);

        return referencedNode;
    }

    public JsonNode get(String refString) throws JsonPointerException {
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
