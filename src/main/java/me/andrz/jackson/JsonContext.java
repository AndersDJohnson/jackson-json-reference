package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;

/**
 * Created by Anders on 10/9/2014.
 */
public class JsonContext {

    JsonNode node;
    String path;

    public JsonContext() {}

    public JsonContext(JsonNode node, String path) {
        this.node = node;
        this.path = path;
    }

    public JsonNode getNode() {
        return node;
    }

    public void setNode(JsonNode node) {
        this.node = node;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JsonContext clone() {
        JsonContext context = new JsonContext();
        context.setNode(node);
        context.setPath(path);
        return context;
    }
}
