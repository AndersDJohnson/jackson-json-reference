package me.andrz.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author slasch
 * @since 09.11.2015.
 */
class YamlObjectMapperFactory implements ObjectMapperFactory {
    private YamlObjectMapperFactory() {}

    @Override
    public ObjectMapper create() {
        return new ObjectMapper(new YAMLFactory());
    }
    public static final YamlObjectMapperFactory instance = new YamlObjectMapperFactory();
}
