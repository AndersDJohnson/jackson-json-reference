package me.andrz.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

/**
 * Used to configure JsonContext and JsonReferenceProcessor
 */
public interface ObjectMapperFactory {

    /**
     * Creates configured {@link ObjectMapper}
     *
     * @param url URL
     * @return configured {@link ObjectMapper}
     */
    public ObjectMapper create(URL url);

}
