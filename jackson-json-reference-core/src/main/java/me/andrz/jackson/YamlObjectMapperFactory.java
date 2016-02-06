package me.andrz.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.net.URL;

/**
 * Use for YAML exclusively.
 */
class YamlObjectMapperFactory implements ObjectMapperFactory {
    private YamlObjectMapperFactory() {}

    @Override
    public ObjectMapper create(URL url) {
        return new ObjectMapper(new YAMLFactory());
    }

    public static final YamlObjectMapperFactory instance = new YamlObjectMapperFactory();
}
