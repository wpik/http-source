package com.github.wpik.httpsource.json;

import com.github.wpik.httpsource.Headers;
import com.github.wpik.httpsource.HttpSourceJsonProperties;
import com.github.wpik.httpsource.HttpSourceProperties;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.HeaderEnricherSpec;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class JsonPathKeyExtractorConfig {

    public static final String JSON_PATH_KEY_EXTRACTOR_BEAN_NAME = "jsonPathKeyExtractor";

    @ConditionalOnProperty(HttpSourceJsonProperties.HTTP_JSON_KEY_EXPRESSION)
    @Bean(JSON_PATH_KEY_EXTRACTOR_BEAN_NAME)
    Consumer<HeaderEnricherSpec> jsonPathKeyExtractor(HttpSourceProperties httpSourceProperties) {
        return e -> e.headerFunction(Headers.KEY_BYTES,
                message -> extractFromPayload(
                        (String) message.getPayload(),
                        httpSourceProperties.getJson().getKeyExpression()
                )
        );
    }

    private byte[] extractFromPayload(String payload, String jsonPath) {
        try {
            Object key = JsonPath.read(payload, jsonPath);
            log.debug("Extracted key using json path expression is '{}'", key);
            return key == null ? null : key.toString().getBytes();
        } catch (PathNotFoundException e) {
            String errorMessage = String.format("Couldn't extract key from request using Json Path '%s'", jsonPath);
            log.warn(errorMessage, e);
            throw new RuntimeException(errorMessage);
        }
    }

    @ConditionalOnProperty(value = HttpSourceJsonProperties.HTTP_JSON_KEY_EXPRESSION, matchIfMissing = true)
    @Bean(JSON_PATH_KEY_EXTRACTOR_BEAN_NAME)
    Consumer<HeaderEnricherSpec> defaultJsonPathKeyExtractor() {
        return e -> e.headerFunction(Headers.KEY_BYTES, (message) -> null);
    }
}
