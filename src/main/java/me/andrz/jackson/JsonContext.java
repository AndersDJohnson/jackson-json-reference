package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class JsonContext {

    JsonNode node;
    URL url;

    public JsonContext() {}

    /**
     * No path context, may not be able to get file-relative references.
     *
     * @param node
     */
    public JsonContext(JsonNode node) {
        this.node = node;
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
