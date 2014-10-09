import com.fasterxml.jackson.databind.*;
import com.github.fge.jackson.jsonpointer.*;
import me.andrz.jackson.*;
import org.apache.logging.log4j.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.*;
import org.junit.*;

import java.io.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Anders on 10/7/2014.
 */
public class JsonReferenceTest {

    private static final Logger logger = LogManager.getLogger(JsonReferenceTest.class);

    static Server server;

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
        String fragment = "/a";
        String ref = uri + "#" + fragment;

        JsonReference jsonReference = new JsonReference(ref);

        assertThat(jsonReference.getUri(), equalTo(uri));
        assertThat(jsonReference.getFragment(), equalTo(fragment));
    }

    @Test
    public void testResolveURL() throws IOException, JsonPointerException {

        String ref = "http://localhost:8080/a.json#/a";

        JsonReference jsonReference = new JsonReference(ref);

        JsonNode jsonNode = jsonReference.resolve();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testResolveFile() throws IOException, JsonPointerException {

        String ref = "src/test/resources/a.json#/a";

        JsonReference jsonReference = new JsonReference(ref);

        JsonNode jsonNode = jsonReference.resolve();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testResolveFileRelativeTo() throws IOException, JsonPointerException {

        String ref = "resources/a.json#/a";

        JsonReference jsonReference = new JsonReference(ref);
        jsonReference.setRelativeTo("src/test");

        JsonNode jsonNode = jsonReference.resolve();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

    @Test
    public void testProcess() throws IOException, JsonReferenceException, JsonPointerException {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(new File("src/test/resources/nest.json"));

        JsonContext context = new JsonContext();
        context.setNode(node);
        context.setPath("src/test/resources");
        JsonReference.process(context);

        String json = mapper.writeValueAsString(node);
        logger.debug("json: " + json);
    }

    @Test
    public void testProcessFromFile() throws IOException, JsonReferenceException, JsonPointerException {

        File file = new File("src/test/resources/nest.json");

        JsonContext context = JsonContext.fromFile(file);
        JsonReference.process(context);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = context.getNode();
        String json = mapper.writeValueAsString(node);
        logger.debug("json: " + json);
    }

    @Test
    public void testResolveRootJson() throws IOException, JsonPointerException {

        String jsonString = "{\"a\": 3}";
        String ref = "#/a";

        JsonReference jsonReference = new JsonReference(ref, jsonString);

        JsonNode jsonNode = jsonReference.resolve();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

}
