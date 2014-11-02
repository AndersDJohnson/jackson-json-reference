package me.andrz.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Represents a JSON node and its location (URL, file, etc).
 */
public class JsonContext {

    JsonNode node;
    URL url;

    public JsonContext() {}

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

        File parent = file.getParentFile();
        URL url = parent.toURI().toURL();
        this.url = url;
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
        this.url = url;
    }

    public JsonNode getNode() {
        return node;
    }

    public void setNode(JsonNode node) {
        this.node = node;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
