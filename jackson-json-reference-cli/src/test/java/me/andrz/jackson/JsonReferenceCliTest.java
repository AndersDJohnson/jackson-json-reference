package me.andrz.jackson;

import org.junit.Test;

import java.io.File;

/**
 * Created by anders on 11/21/15.
 */
public class JsonReferenceCliTest {

    private static final String rootClassPath = JsonReferenceCliTest.class.getResource("/").getFile();

    private static String resource(String path) {
        return rootClassPath + path;
    }

    @Test
    public void testMainHelp() throws Exception {
        JsonReferenceCli.main(new String[] { "-help" });
    }

    @Test
    public void testMainFile() throws Exception {
        JsonReferenceCli.main(new String[] { resource("a.json") });
    }
}
