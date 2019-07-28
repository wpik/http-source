package com.github.wpik.httpsource.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wpik.httpsource.Headers;
import com.github.wpik.httpsource.HttpSourcePojoProperties;
import com.github.wpik.httpsource.HttpSourceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.function.Consumer;

@Configuration
public class PojoDeserializerConfig {

    public static final String POJO_DESERIALIZER_BEAN_NAME = "pojoDeserializer";

    @ConditionalOnProperty(HttpSourcePojoProperties.HTTP_POJO_CLASS_NAME)
    @Bean(POJO_DESERIALIZER_BEAN_NAME)
    Consumer<HeaderEnricherSpec> pojoDeserializer(HttpSourceProperties httpSourceProperties, ObjectMapper objectMapper) {
        return ec -> ec.messageProcessor(m ->
        {
            try {
                return Collections.singletonMap(
                        Headers.DESERIALIZED_OBJECT,
                        objectMapper.readValue((String) m.getPayload(), httpSourceProperties.getPojo().getClassName()));
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.join(", ", e.getOriginalMessage()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @ConditionalOnProperty(name = HttpSourcePojoProperties.HTTP_POJO_CLASS_NAME, matchIfMissing = true)
    @Bean(POJO_DESERIALIZER_BEAN_NAME)
    Consumer<HeaderEnricherSpec> defaultPojoDeserializer() {
        return ec -> ec.messageProcessor(m -> Collections.emptyMap());
    }
}
