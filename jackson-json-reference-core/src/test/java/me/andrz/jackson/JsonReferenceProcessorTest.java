package me.andrz.jackson;

import com.fasterxml.jackson.databind.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 *
 */
public class JsonReferenceProcessorTest {

    private static final Logger logger = LoggerFactory.getLogger(JsonReferenceProcessorTest.class);

    private static Server server;

    private static final String IGNORE_OFFLINE = "ignoring offline";

    private static final String JSON_SCHEMA_URL = "http://json-schema.org/schema";
    /**
     * Length in bytes of JSON at {@link this.JSON_SCHEMA_URL}.
     */
    private static final long JSON_SCHEMA_LENGTH = 1024L * 2; // 2 KB

    private static final String rootClassPath = JsonReferenceProcessorTest.class.getResource("/").getFile();

    private static String resource(String path) {
        return rootClassPath + path;
    }

    private static File resourceAsFile(String path) {
        return new File(resource(path));
    }

    private static URL resourceAsURL(String path) throws MalformedURLException {
        return resourceAsFile(path).toURI().toURL();
    }

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

        File file = resourceAsFile("nest.json");
        String expected = "{\"a\":3,\"b\":4,\"c\":{\"q\":{\"$ref\":\"a.json#\"}},\"nest\":[{\"ok\":\"yes\",\"why\":{\"b\":4}},\"a\"]}";

        JsonNode node = (new JsonReferenceProcessor()).process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

//    @Ignore(IGNORE_OFFLINE)
    @Test
    public void testProcessFileWithRemote() throws IOException, JsonReferenceException {

        File file = resourceAsFile("remote.json");

        JsonNode node = (new JsonReferenceProcessor()).process(file);

        ObjectMapper mapper = new ObjectMapper();

        File outFile = new File("out.json");
        mapper.writeValue(outFile, node);

        // expecting the output to be of a certain length
        long fileLength = file.length();
        assertThat(outFile.length(), greaterThan(fileLength));
        assertThat(outFile.length(), greaterThan(JSON_SCHEMA_LENGTH));
    }

//    @Ignore(IGNORE_OFFLINE)
    @Test
    public void testProcessFileWithRemoteCircularDeep() throws IOException, JsonReferenceException {

        File file = resourceAsFile("remote.json");

        JsonReferenceProcessor ref = new JsonReferenceProcessor();
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

//    @Ignore(IGNORE_OFFLINE)
    @Test
    public void testProcessURLRemote() throws IOException, JsonReferenceException {

        URL url = new URL(JSON_SCHEMA_URL);

        JsonNode node = (new JsonReferenceProcessor()).process(url);

        ObjectMapper mapper = new ObjectMapper();

        File outFile = new File("out.json");
        mapper.writeValue(outFile, node);

        // expecting the output to be of a certain length
        assertThat(outFile.length(), greaterThan(JSON_SCHEMA_LENGTH));
    }

    @Test
    public void testProcessURL() throws IOException, JsonReferenceException {

        URL url = new URL("http://localhost:8080/ref.json");

        JsonNode node = (new JsonReferenceProcessor()).process(url);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);
        assertThat(json, equalTo("{\"q\":{\"a\":3}}"));
    }

    @Test
    public void testGet() throws IOException, JsonReferenceException {

        String refString = "http://localhost:8080/a.json#/a";

        JsonReference ref = JsonReference.fromString(refString);

        JsonNode jsonNode = (new JsonReferenceProcessor()).get(ref);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testGetFromFile() throws IOException, JsonReferenceException {

        File file = resourceAsFile("a.json");

        JsonNode jsonNode = (new JsonReferenceProcessor()).read(file).at("/a");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testGetFromJarFile() throws IOException, JsonReferenceException {

        URL jarFileURL = new URL("jar:file:" + resource("a.jar") + "!/a.json");

        JsonNode jsonNode = (new JsonReferenceProcessor()).read(jarFileURL).at("/a");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testProcessNestedConflicts() throws IOException, JsonReferenceException {

        File file = resourceAsFile("nest-conflict.json");
        String expected = "{\"a\":3,\"b\":{\"c\":3},\"c\":3}";

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        processor.setMaxDepth(-1);
        processor.setMapperFactory(YamlObjectMapperFactory.instance);
        JsonNode node = processor.process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testProcessYamlFileWithNestedMixedTypeScopes() throws IOException, JsonReferenceException {

        File file = resourceAsFile("nest-yaml.yaml");
        String expected = "{\"a\":3,\"b\":4,\"c\":{\"q\":{\"a\":3}},\"nest\":[{\"ok\":true,\"why\":{\"b\":4}},\"a\"],\"d\":{\"e\":{\"f\":3}},\"e\":3,\"f\":3,\"g\":{\"f\":3},\"h\":3}";

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        processor.setMaxDepth(-1);
        processor.setMapperFactory(YamlObjectMapperFactory.instance);
        JsonNode node = processor.process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testProcessYamlFileWithSelfCircularity() throws IOException, JsonReferenceException {

        File file = resourceAsFile("circular-self.yaml");
        String expected = "{\"swagger\":\"2.0\",\"info\":{\"version\":\"0.0.0\",\"title\":\"API\"},\"paths\":{\"/api\":{\"post\":{\"parameters\":[{\"in\":\"body\",\"name\":\"foo\",\"schema\":{\"type\":\"object\",\"properties\":{\"foo\":{\"$ref\":\"#/definitions/foo\"}}}}],\"responses\":{\"200\":{\"description\":\"response\"}}}}},\"definitions\":{\"foo\":{\"type\":\"object\",\"properties\":{\"foo\":{\"type\":\"object\",\"properties\":{\"foo\":{\"$ref\":\"#/definitions/foo\"}}}}}}}";

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        processor.setMaxDepth(2);
        JsonNode node = processor.process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testDetectYamlFile() throws JsonReferenceException, IOException {
        File file = resourceAsFile("a.yaml");
        String expected = "{\"a\":3}";

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        JsonNode node = processor.process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testDetectYamlUrl() throws JsonReferenceException, IOException {
        URL url = resourceAsURL("a.yaml");
        String expected = "{\"a\":3}";

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        JsonNode node = processor.process(url);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testPreserveReferences() throws IOException, JsonReferenceException {

        File file = resourceAsFile("nest.json");
        String expected = "{\"a\":3,\"b\":4,\"c\":{\"q\":{\"$ref\":\"a.json#\"},\"x-$ref\":\"ref.json#\"},\"nest\":[{\"ok\":\"yes\",\"why\":{\"b\":4,\"x-$ref\":\"b.json#\"}},\"a\"]}";

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        processor.setPreserveRefs(true);
        JsonNode node = processor.process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);

        assertThat(json, equalTo(expected));
    }

    @Test
    public void testReplaceTopLevelReference() throws IOException, JsonReferenceException {

        File file = resourceAsFile("top.json");

        JsonReferenceProcessor processor = new JsonReferenceProcessor();
        JsonNode node = processor.process(file);

        String json = new ObjectMapper().writeValueAsString(node);

        String expected = "{\"a\":3}";
        assertThat(json, equalTo(expected));
    }
}
