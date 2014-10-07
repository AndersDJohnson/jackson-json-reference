import com.fasterxml.jackson.databind.*;
import com.github.fge.jackson.jsonpointer.*;
import me.andrz.jackson.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.junit.*;

import java.io.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created by Anders on 10/7/2014.
 */
public class JsonReferenceTest {

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
    public void testResolveRootJson() throws IOException, JsonPointerException {

        String jsonString = "{\"a\": 3}";
        String ref = "#/a";

        JsonReference jsonReference = new JsonReference(ref);
        jsonReference.setRootJsonNode(jsonString);

        JsonNode jsonNode = jsonReference.resolve();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonNode);

        assertThat(json, equalTo("3"));
    }

}
