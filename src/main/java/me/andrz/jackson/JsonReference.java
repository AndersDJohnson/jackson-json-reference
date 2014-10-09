package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import com.github.fge.jackson.jsonpointer.*;
import org.apache.commons.collections4.*;
import org.apache.commons.collections4.functors.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

/**
 * Created by Anders on 10/7/2014.
 */
public class JsonReference {

    public static final Pattern pattern = Pattern.compile("([^\\#]*)(\\#(.*))?");

    private ObjectMapper mapper = new ObjectMapper();


    private String uri;
    private String fragment;
    private JsonNode context;
    private String relativeTo;

    public JsonReference (String ref) {
        this.parseReference(ref);
    }

    public JsonReference (String ref, JsonNode context) {
        this.context = context;
        this.parseReference(ref);
    }

    public JsonReference (String ref, String context) throws IOException {
        JsonNode node = mapper.readTree(context);
        this.context = node;
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

    public JsonNode resolve() throws IOException, JsonPointerException {

        JsonNode referencedJsonNode = getReferencedJsonNode();

        JsonPointer jsonPointer = new JsonPointer(fragment);
        JsonNode refJsonNode = jsonPointer.get(referencedJsonNode);

        return refJsonNode;
    }

    public static void process(JsonContext context) {

        JsonNode node = context.getNode();

//        final JsonNode finalNode = JsonNodeClone.copy(node);
        final JsonContext finalContext = context.clone();

        JsonNodeTraverse.traverse(node, new CatchAndRethrowClosure<JsonNode>() {
            public void executeAndThrow(JsonNode subNode) throws JsonReferenceException, IOException, JsonPointerException {

                if (subNode.isArray()) {
                    ArrayNode arrayNode = (ArrayNode) subNode;
                    Iterator<JsonNode> elements = arrayNode.elements();
                    int i = 0;
                    while (elements.hasNext()) {
                        JsonNode value = elements.next();

                        if (value.has("$ref")) {
                            JsonNode replacement = getReplacement(value, finalContext);
                            System.out.println("replacing " + value + " with " + replacement);
                            arrayNode.set(i, replacement);
                            ++i;
                        }
                    }
                }
                else if (subNode.isObject()) {
                    ObjectNode objectNode = (ObjectNode) subNode;

                    Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        String key = field.getKey();
                        JsonNode value = field.getValue();

                        System.out.println("key=" + key);

                        if (value.has("$ref")) {
                            JsonNode replacement = getReplacement(value, finalContext);
                            System.out.println("replacing " + value + " with " + replacement);
                            objectNode.set(key, replacement);
                        }
                    }
                }
            }
        });

    }

    private static JsonNode getReplacement(JsonNode node, JsonContext finalContext) throws JsonReferenceException, IOException, JsonPointerException {
        JsonNode ref = node.get("$ref");
        if (! ref.isTextual()) {
            throw new JsonReferenceException("$ref not textual for node=" + node);
        }
        String refString = ref.textValue();

        JsonReference jsonReference = new JsonReference(refString);
        jsonReference.setRelativeTo(finalContext.getPath());
        JsonNode replacement = jsonReference.resolve();

        return replacement;
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
            referencedJsonNode = context;
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

    public JsonNode getContext() {
        return context;
    }

    public void setContext(JsonNode context) {
        this.context = context;
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
