package me.andrz.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used to configure JsonContext and JsonReferenceProcessor
 *
 * @author slasch
 * @since 09.11.2015.
 */
public interface ObjectMapperFactory {

    /**
     * Creates configured {@link ObjectMapper}
     *
     * @return configured {@link ObjectMapper}
     */
    ObjectMapper create();
}
