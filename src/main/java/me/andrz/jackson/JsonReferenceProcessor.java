package me.andrz.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class JsonReferenceProcessor {

    private static final Logger logger = LoggerFactory.getLogger(JsonReferenceProcessor.class);

    private ObjectMapper mapper;
    private int maxDepth = 1;
    private boolean stopOnCircular = true;

    public JsonFactory getFactory() { return factory; }

    public void setFactory(JsonFactory factory) { this.factory = factory; }

    public JsonReferenceProcessor withFactory(JsonFactory factory) {
        setFactory(factory);
        return this;
    }

    private JsonFactory factory = new JsonFactory();

    public JsonReferenceProcessor() {
        mapper = new ObjectMapper();
    }

    public JsonReferenceProcessor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public boolean isStopOnCircular() {
        return stopOnCircular;
    }

    public void setStopOnCircular(boolean stopOnCircular) {
        this.stopOnCircular = stopOnCircular;
    }

    public JsonNode process(File file) throws JsonReferenceException, IOException {
        JsonContext context = new JsonContext(file);
        if (factory != null) context.withFactory(factory);
        process(context);
        return context.getNode();
    }

    public JsonNode process(URL url) throws JsonReferenceException, IOException {
        JsonContext context = new JsonContext(url);
        process(context);
        return context.getNode();
    }

    public JsonNode process(JsonContext context) throws JsonReferenceException, IOException {
        JsonNode node = context.getNode();
        process(context, node);
        return context.getNode();
    }

    public JsonNode process(JsonContext context, Set<JsonReference> processed) throws JsonReferenceException, IOException {
        JsonNode node = context.getNode();
        process(context, node, processed);
        return context.getNode();
    }

    public void process(JsonContext context, JsonNode node) throws JsonReferenceException, IOException {
        process(context, node, null);
    }

    public void process(JsonContext context, JsonNode node, Set<JsonReference> processed) throws JsonReferenceException, IOException {

        if (node == null) {
            return;
        }

        if (processed == null) {
            processed = new HashSet<JsonReference>();
        }

        logger.debug("processed: " + processed);

        if (processed.size() >= maxDepth) {
            return;
        }

        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            Iterator<JsonNode> elements = arrayNode.elements();
            int i = 0;
            while (elements.hasNext()) {
                JsonNode subNode = elements.next();

                logger.debug("i=" + i);

                if (JsonReferenceNode.is(subNode)) {

                    JsonNode replacementNode = getReplacementNode(subNode, context, processed);
                    if (replacementNode == null) continue;

                    logger.debug("replacing " + "subNode" + " with " + replacementNode);
                    arrayNode.set(i, replacementNode);
                    ++i;
                }
                else {
                    process(context, subNode, processed);
                }
            }
        }
        else if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;

            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                JsonNode subNode = field.getValue();

                logger.debug("key=" + key);

                if (JsonReferenceNode.is(subNode)) {

                    JsonNode replacementNode = getReplacementNode(subNode, context, processed);
                    if (replacementNode == null) continue;

                    logger.debug("replacing " + "subNode" + " with " + replacementNode);
                    objectNode.set(key, replacementNode);
                }
                else {
                    process(context, subNode, processed);
                }
            }
        }
    }

    public JsonNode getReplacementNode(JsonNode node, JsonContext context, Set<JsonReference> processed) throws JsonReferenceException, IOException {

        JsonReferenceNode refNode = JsonReferenceNode.fromNode(node);
        JsonReference ref = refNode.getJsonReference();
        JsonReference absRef = getAbsoluteRef(ref, context);

        if (stopOnCircular && processed.contains(absRef)) {
            logger.debug("skipping on ref: " + absRef);
            return null;
        }
        // add ref to processed set for detection loop in recursion
        processed.add(absRef);

        JsonContext referencedContext = resolveFromContextToContext(ref, context);

        // recurse
        process(referencedContext, processed);
        // after recursing, remove ref from processed set for next iteration
        processed.remove(absRef);

        JsonNode replacementNode = referencedContext.getNode();

        return replacementNode;
    }

    public JsonReference getAbsoluteRef(JsonReference ref, JsonContext context) throws JsonReferenceException {
        // TODO: More robust URL building.
        String newRefString = context.getUrl() + "#" + ref.getPointer().toString();
        JsonReference newRef = JsonReference.fromString(newRefString);
        return newRef;
    }

    public JsonContext resolveFromContextToContext(JsonReference ref, JsonContext context) throws IOException, JsonReferenceException {

        JsonContext referencedContext;
        JsonNode referencedNode;

        URL absoluteReferencedUrl;
        URI refUri = ref.getUri();

        logger.debug("dereferencing " + ref);

        if (ref.isLocal()) {
            absoluteReferencedUrl = context.getUrl();
            ObjectMapper mapper = factory == null ? new ObjectMapper() : new ObjectMapper(factory);
            JsonNode clone = mapper.readTree(context.getNode().traverse());
            referencedNode = clone.at(ref.getPointer());
        }
        else if (ref.isAbsolute()) {
            absoluteReferencedUrl = refUri.toURL();
            referencedNode = read(absoluteReferencedUrl).at(ref.getPointer());
        }
        else {
            URL contextUrl = context.getUrl();
            try {
                absoluteReferencedUrl = contextUrl.toURI().resolve(refUri).toURL();
            } catch (URISyntaxException e) {
                throw new JsonReferenceException("Invalid URI for context URL: " + contextUrl);
            }
            referencedNode = read(absoluteReferencedUrl).at(ref.getPointer());
        }

        referencedContext = new JsonContext();
        referencedContext.setUrl(absoluteReferencedUrl);
        referencedContext.setNode(referencedNode);

        referencedContext.setUrl(absoluteReferencedUrl);

        return referencedContext;
    }

    /**
     * Resolve with defaults.
     * - Assumes ref points to absolute URL.
     *
     * @param ref
     * @return
     * @throws IOException
     */
    public JsonNode get(JsonReference ref) throws IOException {
        JsonNode referencedNode;

        URI refUri = ref.getUri();
        URL url = refUri.toURL();

        referencedNode = read(url).at(ref.getPointer());

        return referencedNode;
    }

    public JsonNode read(URL url) throws IOException {
        return mapper.readTree(url);
    }

    public JsonNode read(File file) throws IOException {
        return mapper.readTree(file);
    }

    public JsonNode readFile(String fileString) throws IOException {
        File file = new File(fileString);
        return read(file);
    }

}
