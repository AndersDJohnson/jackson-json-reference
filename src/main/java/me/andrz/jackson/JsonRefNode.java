package me.andrz.jackson;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represent a JSON Reference object node.
 */
public class JsonRefNode {

    private ObjectNode objectNode;

    public JsonRefNode(JsonNode jsonNode) {
        this.objectNode = (ObjectNode) jsonNode;
    }

    public String getRefString() throws JsonReferenceException {
        JsonNode ref = objectNode.get("$ref");
        if (! ref.isTextual()) {
            throw new JsonReferenceException("$ref not textual for node=" + objectNode);
        }
        String refString = ref.textValue();
        return refString;
    }

    public static boolean is(JsonNode node) {
        return node.has("$ref");
    }

}
