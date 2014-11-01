package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.github.fge.jackson.jsonpointer.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 */
public class JsonReference {

    private static final Logger logger = LogManager.getLogger(JsonReference.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode process(File file) throws JsonReferenceException, IOException, JsonPointerException {
        JsonContext context = new JsonContext(file);
        process(context);
        return context.getNode();
    }

    public static JsonNode process(URL url) throws JsonReferenceException, IOException, JsonPointerException {
        JsonContext context = new JsonContext(url);
        process(context);
        return context.getNode();
    }

    public static JsonNode process(JsonContext context) throws JsonReferenceException, IOException, JsonPointerException {
        JsonNode node = context.getNode();
        process(context, node);
        return context.getNode();
    }

    public static void process(JsonContext context, JsonNode node) throws JsonReferenceException, IOException, JsonPointerException {

        int depth = 100;

        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            Iterator<JsonNode> elements = arrayNode.elements();
            int i = 0;
            while (elements.hasNext()) {
                JsonNode subNode = elements.next();

                if (JsonRefNode.is(subNode)) {
                    JsonRef ref = getJsonRefForJsonNode(subNode);
                    JsonContext referencedContext = resolveFromContextToContext(ref, context);

                    JsonNode replacementNode = referencedContext.getNode();

                    logger.debug("replacing " + subNode + " with " + replacementNode);
                    arrayNode.set(i, replacementNode);
                    ++i;
                }
                else {
                    process(context, subNode);
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
                    JsonRef ref = getJsonRefForJsonNode(subNode);
                    JsonContext referencedContext = resolveFromContextToContext(ref, context);

                    if (depth > 0) {
                        process(referencedContext);
                    }

                    JsonNode replacementNode = referencedContext.getNode();

                    logger.debug("replacing " + subNode + " with " + replacementNode);
                    objectNode.set(key, replacementNode);
                }
                else {
                    process(context, subNode);
                }
            }
        }
    }

    public static JsonRef getJsonRefForJsonNode(JsonNode node) throws JsonReferenceException {
        JsonRefNode refNode = new JsonRefNode(node);
        String refString = refNode.getRefString();
        JsonRef ref = new JsonRef(refString);
        return ref;
    }

    public static JsonContext resolveFromContextToContext(JsonRef ref, JsonContext context) throws IOException, JsonPointerException {

        JsonContext referencedContext;
        JsonNode referencedNode;

        URL absoluteReferencedUrl;
        String refUri = ref.getUri();

        logger.debug("dereferencing " + ref);

        if (ref.isForAbsoluteUrl()) {
            absoluteReferencedUrl = new URL(refUri);
        }
        else {
            URL contextUrl = context.getUrl();
            absoluteReferencedUrl = new URL(contextUrl, refUri);
        }

        referencedNode = from(absoluteReferencedUrl).get(ref);

        referencedContext = new JsonContext();
        referencedContext.setUrl(absoluteReferencedUrl);
        referencedContext.setNode(referencedNode);

        referencedContext.setUrl(absoluteReferencedUrl);

        return referencedContext;
    }

    public static JsonNode resolveFromContextToNode(JsonRef ref, JsonContext context) throws IOException, JsonPointerException {

        JsonContext toContext = resolveFromContextToContext(ref, context);
        JsonNode referencedNode;

        referencedNode = toContext.getNode();

        return referencedNode;
    }

    /**
     * Resolve with defaults.
     * - Assumes ref points to absolute URL.
     *
     * @param ref
     * @return
     * @throws IOException
     * @throws JsonPointerException
     */
    public static JsonNode get(JsonRef ref) throws IOException, JsonPointerException {
        JsonNode referencedNode;

        String refUri = ref.getUri();

        URL url = new URL(refUri);

        referencedNode = from(url).get(ref);

        return referencedNode;
    }

    public static ReferringJsonNode from(URL url) throws IOException {
        JsonNode node = mapper.readTree(url);
        return from(node);
    }

    public static ReferringJsonNode from(File file) throws IOException {
        return fromFile(file);
    }

    public static ReferringJsonNode from(JsonNode node) {
        return new ReferringJsonNode(node);
    }

    public static ReferringJsonNode fromFile(File file) throws IOException {
        JsonNode node = mapper.readTree(file);
        return from(node);
    }

    public static ReferringJsonNode fromFile(String fileString) throws IOException {
        File file = new File(fileString);
        return fromFile(file);
    }

}
