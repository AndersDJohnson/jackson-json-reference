package me.andrz.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class JsonReference {

    private static final Logger logger = LogManager.getLogger(JsonReference.class);

    private ObjectMapper mapper;
    private int maxDepth = 1;
    private boolean stopOnCircular = true;

    public JsonReference() {
        mapper = new ObjectMapper();
    }

    public JsonReference(ObjectMapper mapper) {
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

    public JsonNode process(JsonContext context, Set<JsonRef> processed) throws JsonReferenceException, IOException {
        JsonNode node = context.getNode();
        process(context, node, processed);
        return context.getNode();
    }

    public void process(JsonContext context, JsonNode node) throws JsonReferenceException, IOException {
        process(context, node, null);
    }

    public void process(JsonContext context, JsonNode node, Set<JsonRef> processed) throws JsonReferenceException, IOException {

        if (node == null) {
            return;
        }

        if (processed == null) {
            processed = new HashSet<JsonRef>();
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

                if (JsonRefNode.is(subNode)) {

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

                if (JsonRefNode.is(subNode)) {

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

    public JsonNode getReplacementNode(JsonNode subNode, JsonContext context, Set<JsonRef> processed) throws JsonReferenceException, IOException {

        JsonRef ref = getJsonRefForJsonNode(subNode);
        JsonRef absRef = getAbsoluteRef(ref, context);
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

    public JsonRef getAbsoluteRef(JsonRef ref, JsonContext context) {
        JsonRef clone = ref.clone();
        clone.setUri(context.getUrl().toString());
        return clone;
    }

    public JsonRef getJsonRefForJsonNode(JsonNode node) throws JsonReferenceException {
        JsonRefNode refNode = new JsonRefNode(node);
        String refString = refNode.getRefString();
        JsonRef ref = new JsonRef(refString);
        return ref;
    }

    public JsonContext resolveFromContextToContext(JsonRef ref, JsonContext context) throws IOException {

        JsonContext referencedContext;
        JsonNode referencedNode;

        URL absoluteReferencedUrl;
        String refUri = ref.getUri();

        logger.debug("dereferencing " + ref);

        if (ref.isForLocal()) {
            absoluteReferencedUrl = context.getUrl();
            JsonNode clone = new ObjectMapper().readTree(context.getNode().traverse());
            referencedNode = from(clone).get(ref);
        }
        else if (ref.isForAbsoluteUrl()) {
            absoluteReferencedUrl = new URL(refUri);
            referencedNode = from(absoluteReferencedUrl).get(ref);
        }
        else {
            URL contextUrl = context.getUrl();
            absoluteReferencedUrl = new URL(contextUrl, refUri);
            referencedNode = from(absoluteReferencedUrl).get(ref);
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
    public JsonNode get(JsonRef ref) throws IOException {
        JsonNode referencedNode;

        String refUri = ref.getUri();

        URL url = new URL(refUri);

        referencedNode = from(url).get(ref);

        return referencedNode;
    }

    public RefResolvingJsonNode from(URL url) throws IOException {
        JsonNode node = mapper.readTree(url);
        return from(node);
    }

    public RefResolvingJsonNode from(File file) throws IOException {
        return fromFile(file);
    }

    public RefResolvingJsonNode from(JsonNode node) {
        return new RefResolvingJsonNode(node);
    }

    public RefResolvingJsonNode fromFile(File file) throws IOException {
        JsonNode node = mapper.readTree(file);
        return from(node);
    }

    public RefResolvingJsonNode fromFile(String fileString) throws IOException {
        File file = new File(fileString);
        return fromFile(file);
    }

}
