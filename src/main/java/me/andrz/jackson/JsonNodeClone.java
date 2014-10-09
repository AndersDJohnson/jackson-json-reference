package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.*;

/**
 * Created by Anders on 10/8/2014.
 */
public class JsonNodeClone {

    public static <T extends JsonNode> T copy(T node) {
        try {
            return (T) new ObjectMapper().readTree(node.traverse());
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
