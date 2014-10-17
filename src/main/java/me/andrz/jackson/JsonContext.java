package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
    public JsonContext(File file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(file);
        this.node = node;

        String parent = file.getParent();
        this.path = parent;
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JsonContext(URL url) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(url);
        this.node = node;
        this.path = url.toString();
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
