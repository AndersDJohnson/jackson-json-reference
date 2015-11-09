package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a JSON node and its location (URL, file, etc).
 */
public class JsonContext {

    JsonNode node;
    URL url;
    JsonFactory jf;

    public JsonContext() {}

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public JsonContext(File file) throws MalformedURLException {
        this.url = file.toURI().toURL();
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JsonContext(URL url) {
        this.url = url;
    }

    private void init() throws IOException {
        ObjectMapper mapper = jf == null ? new ObjectMapper() : new ObjectMapper(jf);
        JsonNode node = mapper.readTree(url);
        this.node = node;
    }

    public JsonNode getNode() throws IOException {
        if (node == null) init();
        return node;
    }

    public JsonContext withFactory(JsonFactory factory) {
        jf = factory;
        return this;
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
