package me.andrz.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;

/**
 * ObjectMapperFactory which produces default ObjectMappers
 */
public class DefaultObjectMapperFactory implements ObjectMapperFactory {
    private DefaultObjectMapperFactory() {}

    @Override
    public ObjectMapper create(URL url) {
        ObjectMapperFactory factory = factoryForURL(url);
        if (factory != null) {
            return factory.create(url);
        }
        return new ObjectMapper();
    }

    public static ObjectMapperFactory factoryForURL(URL url) {
        String path = url.getPath();
        // TODO: Consider using file type detectors and MIME types.
//        String contentType = Files.probeContentType(Paths.get(path));
//        System.out.println(contentType);
        String ext = FilenameUtils.getExtension(path);
        if ("yml".equals(ext) || "yaml".equals(ext)) {
            return YamlObjectMapperFactory.instance;
        }
        return null;
    }

    public static final DefaultObjectMapperFactory instance = new DefaultObjectMapperFactory();
}
