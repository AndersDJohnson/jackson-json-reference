package me.andrz.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ObjectMapperFactory which produces default ObjectMappers
 *
 * @author slasch
 * @since 09.11.2015.
 */
public class DefaultObjectMapperFactory implements ObjectMapperFactory {
    private DefaultObjectMapperFactory() {}

    @Override
    public ObjectMapper create() {
        return new ObjectMapper();
    }
    public static final DefaultObjectMapperFactory instance = new DefaultObjectMapperFactory();
}
