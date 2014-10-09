package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class JsonContext {

    JsonNode node;
    String path;

    public JsonContext() {}

    /**
     * No path context, may not be able to resolve file-relative references.
     *
     * @param node
     */
    public JsonContext(JsonNode node) {
        this.node = node;
    }

    /**
     *
     * @param node
     * @param path
     */
    public JsonContext(JsonNode node, String path) {
        this.node = node;
        this.path = path;
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static JsonContext fromFile(File file) throws IOException {
        JsonContext context = new JsonContext();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(file);
        context.setNode(node);

        String parent = file.getParent();
        context.setPath(parent);

        return context;
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

}
