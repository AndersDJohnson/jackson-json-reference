package me.andrz.jackson;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represent a JSON Reference object node (one with a '$ref' key).
 */
public class JsonRefNode {

    private JsonNode jsonNode;
    private JsonReference jsonReference;

    private JsonRefNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public static JsonRefNode fromNode(JsonNode jsonNode) throws JsonReferenceException {
        JsonRefNode jsonRefNode = new JsonRefNode(jsonNode);

        String refString = getRefString(jsonNode);
        JsonReference jsonReference = JsonReference.fromString(refString);

        jsonRefNode.jsonReference = jsonReference;

        return jsonRefNode;
    }

    protected static String getRefString(JsonNode jsonNode) throws JsonReferenceException {
        JsonNode $refNode = jsonNode.get("$ref");
        if ($refNode == null) { // this does not imply null value
            throw new JsonReferenceException("Node does not have \"$ref\" property. node=" + jsonNode);
        }
        if (! $refNode.isTextual()) {
            throw new JsonReferenceException("\"$ref\" value is not textual for node=" + jsonNode);
        }
        String refString = $refNode.textValue();
        return refString;
    }

    public static boolean is(JsonNode node) {
        return node.has("$ref") && node.get("$ref").isTextual();
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }

    public JsonReference getJsonReference() {
        return jsonReference;
    }

}
