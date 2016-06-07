package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a JSON node, its location (URL, file, etc) and a parent context if any
 */
public class JsonContext {

    JsonNode node;
    URL url;
    ObjectMapperFactory jf;
    private JsonNode document;
    private Set<JsonReference> processed;
    private final int depth;

    public JsonContext(int depth) {
        this.depth = depth + 1;
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public JsonContext(File file, int depth) throws IOException {
        this(file.toURI().toURL(), depth);
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JsonContext(URL url, int depth) throws IOException {
        this.url = url;
        this.depth = depth + 1;
    }

    public JsonNode getDocument() throws IOException {
        ObjectMapper mapper = getFactory().create(url);
        document = mapper.readTree(url);
        return document;
    }

    public JsonNode getNode() throws IOException {
        if (node == null) {
            ObjectMapper mapper = getFactory().create(url);
            node = mapper.readTree(url);
        }
        return node;
    }

    /**
     * The {@factory} is used to create ObjectMapper instances
     * If not defined, default will be used
     * @param factory
     */
    public void setFactory(ObjectMapperFactory factory) {
        jf = factory;
    }

    /**
     * Returns user defined ObjectMapperFactory, if one was set or {@DefaultObjectMapperFactory}
     * @return
     */
    public ObjectMapperFactory getFactory() {
        return jf == null ? DefaultObjectMapperFactory.instance : jf;
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

    public Set<JsonReference> getProcessed() {
        return processed;
    }

    public void setProcessed(Set<JsonReference> processed) {
        this.processed = processed;
    }

    /**
     * Look up a node including on document if needed
     *
     * @param pointer
     * @return
     */
    public JsonNode at(JsonPointer pointer) throws IOException {
        return getDocument().at(pointer);
    }

     public int getDepth() {
        return depth;
    }
}
