package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import org.apache.commons.collections4.*;

import java.util.*;

/**
 * Created by Anders on 10/8/2014.
 */
public class JsonNodeTraverse {

    public static void traverse(JsonNode node, Closure<JsonNode> closure) {

        System.out.println(node.getClass().getCanonicalName());

        closure.execute(node);

        if (node.isArray()) {

            Iterator<JsonNode> nodeIterator = node.elements();

            while (nodeIterator.hasNext()) {

                JsonNode subNode = nodeIterator.next();

                System.out.println("value=" + subNode);

                JsonNodeTraverse.traverse(subNode, closure);
            }
        }
        else if (node.isObject()) {

            Iterator<Map.Entry<String, JsonNode>> nodeIterator = node.fields();

            while (nodeIterator.hasNext()) {

                Map.Entry<String, JsonNode> entry = nodeIterator.next();

                String key = entry.getKey();
                JsonNode subNode = entry.getValue();

                System.out.println("key=" + key + "; value=" + subNode);

                JsonNodeTraverse.traverse(subNode, closure);
            }
        }
    }

}
