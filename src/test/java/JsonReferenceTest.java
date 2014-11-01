import com.fasterxml.jackson.databind.*;
import com.github.fge.jackson.jsonpointer.*;
import me.andrz.jackson.*;
import org.apache.logging.log4j.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;
import org.junit.*;

import java.io.*;
import java.net.URL;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Anders on 10/7/2014.
 */
public class JsonReferenceTest {

    private static final Logger logger = LogManager.getLogger(JsonReferenceTest.class);

    static Server server;

    private static final ObjectMapper mapper = new ObjectMapper();

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
    public void testConstruct() throws IOException, JsonPointerException {

        String uri = "http://localhost:8080";
        String pointer = "/a";
        String refString = uri + "#" + pointer;

        JsonRef ref = new JsonRef(refString);

        assertThat(ref.getUri(), equalTo(uri));
        assertThat(ref.getPointer(), equalTo(pointer));
    }

    @Test
    public void testGet() throws IOException, JsonPointerException {

        String refString = "http://localhost:8080/a.json#/a";

        JsonRef ref = new JsonRef(refString);

        JsonNode jsonNode = (new JsonReference()).get(ref);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testGetFromFile() throws IOException, JsonPointerException {

        File file = new File("src/test/resources/a.json");

        JsonNode jsonNode = (new JsonReference()).from(file).get("/a");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testProcessFile() throws IOException, JsonReferenceException, JsonPointerException {

        File file = new File("src/test/resources/nest.json");

        JsonNode node = (new JsonReference()).process(file);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);
        logger.debug("json: " + json);
    }

    @Test
    public void testProcessFileWithRemote() throws IOException, JsonReferenceException, JsonPointerException {

        File file = new File("src/test/resources/remote.json");

        JsonNode node = (new JsonReference()).process(file);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File("out.json"), node);
    }

    @Test
    public void testProcessFileWithRemoteCircularDeep() throws IOException, JsonReferenceException, JsonPointerException {

        File file = new File("src/test/resources/remote.json");

        JsonReference ref = new JsonReference();
        ref.setStopOnCircular(false);
        ref.setMaxDepth(2);
        JsonNode node = ref.process(file);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File("out.json"), node);
    }

    @Test
    public void testProcessURL() throws IOException, JsonReferenceException, JsonPointerException {

        URL url = new URL("http://localhost:8080/ref.json");

        JsonNode node = (new JsonReference()).process(url);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(node);
        logger.debug("json: " + json);
        assertThat(json, equalTo("{\"q\":{\"a\":3}}"));
    }

    @Test
    public void testGetFromJsonNode() throws IOException, JsonPointerException {

        String jsonString = "{\"a\": 3}";
        JsonNode fromNode = mapper.readTree(jsonString);

        String refString = "#/a";
        JsonRef ref = new JsonRef(refString);

        JsonNode toNode = (new JsonReference()).from(fromNode).get(ref);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(toNode);

        assertThat(json, equalTo("3"));
    }

}
