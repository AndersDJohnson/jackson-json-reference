package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a JSON node, its location (URL, file, etc) and a parent context if any
 */
public class JsonContext {

    JsonNode node;
    URL url;
    ObjectMapperFactory jf;
    // sometimes deeply nested context references outer
    // parents allow traversing contexts chain
    // the topmost context should enclose the whole document
    private JsonContext parent;

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
        ObjectMapper mapper = getFactory().create();
        JsonNode node = mapper.readTree(url);
        this.node = node;
    }

    public JsonNode getNode() throws IOException {
        if (node == null) init();
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

    /**
     * Look up a node including enclosing contexts if needed
     *
     * @param pointer
     * @return
     */
    public JsonNode at(JsonPointer pointer) {
        JsonNode optionalNode = node.at(pointer);
        boolean tryParent = optionalNode == MissingNode.getInstance() && parent != null;
        return  tryParent ? parent.at(pointer) : optionalNode.deepCopy();
    }

    /**
     * Creates a child context with this as a parent
     * @param url
     * @return
     */
    public JsonContext child(URL url) {
        JsonContext child = new JsonContext(url);
        child.parent = this;
        return child;
    }
}
