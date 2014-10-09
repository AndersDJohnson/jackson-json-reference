package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.github.fge.jackson.jsonpointer.*;
import org.apache.logging.log4j.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 */
public class JsonReference {

    private static final Logger logger = LogManager.getLogger(JsonReference.class);

    public static final Pattern pattern = Pattern.compile("([^\\#]*)(\\#(.*))?");

    private ObjectMapper mapper = new ObjectMapper();


    private String uri;
    private String fragment;
    private JsonNode contextNode;
    private String relativeTo;

    public JsonReference (String ref) {
        this.parseReference(ref);
    }

    public JsonReference (String ref, JsonNode contextNode) {
        this.contextNode = contextNode;
        this.parseReference(ref);
    }

    public JsonReference (String ref, String contextNodeString) throws IOException {
        JsonNode contextNode = mapper.readTree(contextNodeString);
        this.contextNode = contextNode;
        this.parseReference(ref);
    }

    public void parseReference(String ref) {
        String uri;
        String fragment = null;

        Matcher matcher = pattern.matcher(ref);
        matcher.find();
        uri = matcher.group(1);
        int groupCount = matcher.groupCount();
        if (groupCount > 2) {
            fragment = matcher.group(3);
        }
        this.uri = uri;
        this.fragment = fragment;
    }

    public static void process(JsonContext context) throws JsonReferenceException, IOException, JsonPointerException {
        JsonNode node = context.getNode();
        process(context, node);
    }

    public static void process(JsonContext context, JsonNode node) throws JsonReferenceException, IOException, JsonPointerException {

        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            Iterator<JsonNode> elements = arrayNode.elements();
            int i = 0;
            while (elements.hasNext()) {
                JsonNode subNode = elements.next();

                if (subNode.has("$ref")) {
                    JsonNode replacement = resolveForRefNode(subNode, context);

                    logger.debug("replacing " + subNode + " with " + replacement);
                    arrayNode.set(i, replacement);
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

                if (subNode.has("$ref")) {
                    JsonNode replacement = resolveForRefNode(subNode, context);

                    logger.debug("replacing " + subNode + " with " + replacement);
                    objectNode.set(key, replacement);
                }
                else {
                    process(context, subNode);
                }
            }
        }

    }

    private static JsonNode resolveForRefNode(JsonNode node, JsonContext finalContext) throws JsonReferenceException, IOException, JsonPointerException {
        JsonNode ref = node.get("$ref");
        if (! ref.isTextual()) {
            throw new JsonReferenceException("$ref not textual for node=" + node);
        }
        String refString = ref.textValue();

        JsonReference jsonReference = new JsonReference(refString);
        jsonReference.setRelativeTo(finalContext.getPath());
        JsonNode replacement = jsonReference.resolve();

        String path = jsonReference.getUri();
        String parentPath = finalContext.getPath();
        File refFile = new File(parentPath, path);
        String refParent = refFile.getParent();

        JsonContext replacementContext = new JsonContext(replacement, refParent);
        // recursive process
        process(replacementContext, replacement);

        return replacement;
    }

    public JsonNode resolve() throws IOException, JsonPointerException {
        JsonNode referencedJsonNode = getReferencedJsonNode();

        JsonPointer jsonPointer = new JsonPointer(fragment);
        JsonNode refJsonNode = jsonPointer.get(referencedJsonNode);

        return refJsonNode;
    }

    private JsonNode getReferencedJsonNode() throws IOException {

        JsonNode referencedJsonNode;

        if (uri != null && ! "".equals(uri)) {
            if (isUriUrl()) {
                URL url = new URL(uri);
                referencedJsonNode = mapper.readTree(url);
            } else {
                String relUri;
                if (relativeTo == null) {
                    relUri = uri;
                } else {
                    relUri = relativeTo + "/" + uri;
                }
                File file = new File(relUri);
                referencedJsonNode = mapper.readTree(file);
            }
        }
        else {
            referencedJsonNode = contextNode;
        }

        return referencedJsonNode;
    }

    public boolean isUriUrl() {
        return uri.matches("^https?://.*");
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public JsonNode getContextNode() {
        return contextNode;
    }

    public void setContextNode(JsonNode contextNode) {
        this.contextNode = contextNode;
    }

    public String getRelativeTo() {
        return relativeTo;
    }

    public void setRelativeTo(String relativeTo) {
        this.relativeTo = relativeTo;
    }

    public void setRelativeToPath(File relativeToPath) {
        this.relativeTo = relativeToPath.getAbsolutePath();
    }
}
