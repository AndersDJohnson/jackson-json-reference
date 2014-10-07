package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import com.github.fge.jackson.jsonpointer.*;

import java.io.*;
import java.net.*;
import java.util.regex.*;

/**
 * Created by Anders on 10/7/2014.
 */
public class JsonReference {

    public static final Pattern pattern = Pattern.compile("([^\\#]*)(\\#(.*))?");

    private ObjectMapper mapper = new ObjectMapper();


    private String uri;
    private String fragment;
    private JsonNode rootJson;
    private String relativeTo;


    public JsonReference (String ref) {

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

    private JsonNode getReferencedJsonNode() throws IOException {

        if (rootJson != null) {
            return rootJson;
        }

        JsonNode referencedJsonNode;

        if (isUriUrl()) {
            URL url = new URL(uri);
            referencedJsonNode = mapper.readTree(url);
        }
        else {
            String relUri;
            if (relativeTo == null) {
                relUri = uri;
            }
            else {
                relUri = relativeTo + "/" + uri;
            }
            File file = new File(relUri);
            referencedJsonNode = mapper.readTree(file);
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

    public JsonNode getRootJson() {
        return rootJson;
    }

    public void setRootJson(JsonNode rootJson) {
        this.rootJson = rootJson;
    }

    public void setRootJsonNode(String rootJsonNode) throws IOException {
        this.rootJson = mapper.readTree(rootJsonNode);
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
