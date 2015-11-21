package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Represents a JSON node, its location (URL, file, etc) and a parent context if any
 */
public class JsonContext {

    JsonNode node;
    URL url;
    ObjectMapperFactory jf;
    private JsonNode document;
    private boolean isDocument;

    public JsonContext() {}

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public JsonContext(File file) throws IOException {
        this(file.toURI().toURL());
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JsonContext(URL url) throws IOException {
        this.url = url;
    }
    public JsonNode getDocument() throws IOException {
        ObjectMapper mapper = getFactory().create();
        document = mapper.readTree(url);
        return document;
    }

    public JsonNode getNode() throws IOException {
        if (node == null) {
            ObjectMapper mapper = getFactory().create();
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

    /**
     * Look up a node including on document if needed
     *
     * @param pointer
     * @return
     */
    public JsonNode at(JsonPointer pointer) throws IOException {
        return getDocument().at(pointer);

//        JsonNode optionalNode = node.at(pointer);
//        boolean tryDocument = optionalNode == MissingNode.getInstance() && getDocument() != null;
//        if (tryDocument) {
//            JsonNode n = getDocument().at(pointer);
//            return n;
//        }
//        else {
//            return optionalNode.deepCopy();
//        }

//        boolean tryDocument = optionalNode == MissingNode.getInstance() && getDocument() != null;
//        return  tryDocument ? getDocument().at(pointer) : optionalNode.deepCopy();
    }

}
