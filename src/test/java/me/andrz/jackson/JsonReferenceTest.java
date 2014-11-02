package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import org.apache.logging.log4j.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;
import org.junit.*;

import java.io.*;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 *
 */
public class JsonReferenceTest {

    private static final Logger logger = LogManager.getLogger(JsonReferenceTest.class);

    private static Server server;

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String JSON_SCHEMA_URL = "http://json-schema.org/schema";
    /**
     * Length in bytes of JSON at [@link JSON_SCHEMA_URL}.
     */
    private static final long JSON_SCHEMA_LENGTH = 1024L * 2; // 2 KB

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new Server(8080);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("src/test/resources");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
//        server.join();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
    }

    @Test
    public void testProcessFile() throws IOException, JsonReferenceException {

        File file = new File("src/test/resources/nest.json");
        String expected = "{\"a\":3,\"b\":4,\"c\":{\"q\":{\"$ref\":\"a.json#\"}},\"nest\":[{\"ok\":\"yes\",\"why\":{\"b\":4}},\"a\"]}";

        JsonNode node = (new JsonReference()).process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testProcessFileWithRemote() throws IOException, JsonReferenceException {

        File file = new File("src/test/resources/remote.json");

        JsonNode node = (new JsonReference()).process(file);

        ObjectMapper mapper = new ObjectMapper();

        File outFile = new File("out.json");
        mapper.writeValue(outFile, node);

        // expecting the output to be of a certain length
        long fileLength = file.length();
        assertThat(outFile.length(), greaterThan(fileLength));
        assertThat(outFile.length(), greaterThan(JSON_SCHEMA_LENGTH));
    }

    @Test
    public void testProcessFileWithRemoteCircularDeep() throws IOException, JsonReferenceException {

        File file = new File("src/test/resources/remote.json");

        JsonReference ref = new JsonReference();
        ref.setStopOnCircular(false);
        ref.setMaxDepth(2);
        JsonNode node = ref.process(file);

        ObjectMapper mapper = new ObjectMapper();

        File outFile = new File("out.json");
        mapper.writeValue(outFile, node);

        // expecting the output to be of a certain length
        long fileLength = file.length();
        assertThat(outFile.length(), greaterThan(fileLength));
        assertThat(outFile.length(), greaterThan(JSON_SCHEMA_LENGTH));
    }

    @Test
    public void testProcessURLRemote() throws IOException, JsonReferenceException {

        URL url = new URL(JSON_SCHEMA_URL);

        JsonNode node = (new JsonReference()).process(url);

        ObjectMapper mapper = new ObjectMapper();

        File outFile = new File("out.json");
        mapper.writeValue(outFile, node);

        // expecting the output to be of a certain length
        assertThat(outFile.length(), greaterThan(JSON_SCHEMA_LENGTH));
    }

    @Test
    public void testProcessURL() throws IOException, JsonReferenceException {

        URL url = new URL("http://localhost:8080/ref.json");

        JsonNode node = (new JsonReference()).process(url);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);
        assertThat(json, equalTo("{\"q\":{\"a\":3}}"));
    }

    @Test
    public void testGet() throws IOException, JsonReferenceException {

        String refString = "http://localhost:8080/a.json#/a";

        JsonRef ref = JsonRef.fromString(refString);

        JsonNode jsonNode = (new JsonReference()).get(ref);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testGetFromFile() throws IOException, JsonReferenceException {

        File file = new File("src/test/resources/a.json");

        JsonNode jsonNode = (new JsonReference()).read(file).at("/a");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

}
