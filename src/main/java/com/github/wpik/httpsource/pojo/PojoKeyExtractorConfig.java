package com.github.wpik.httpsource.pojo;

import com.github.wpik.httpsource.Headers;
import com.github.wpik.httpsource.HttpSourcePojoProperties;
import com.github.wpik.httpsource.HttpSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.dsl.HeaderEnricherSpec;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class PojoKeyExtractorConfig {

    public static final String POJO_KEY_EXTRACTOR_BEAN_NAME = "pojoKeyExtractor";

    @ConditionalOnProperty(HttpSourcePojoProperties.HTTP_POJO_KEY_EXPRESSION)
    @Bean(POJO_KEY_EXTRACTOR_BEAN_NAME)
    Consumer<HeaderEnricherSpec> pojoKeyExtractor(HttpSourceProperties httpSourceProperties) {
        return e -> e.headerFunction(Headers.KEY_BYTES,
                message -> extractFromPayload(
                        message.getHeaders().get(Headers.DESERIALIZED_OBJECT),
                        httpSourceProperties.getPojo().getKeyExpression()
                )
        );
    }

    private byte[] extractFromPayload(Object deserialized, String keyExpression) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(keyExpression);

            Object key = exp.getValue(deserialized);

            log.debug("Extracted key using pojo key expression is '{}'", key);

            return key == null ? null : key.toString().getBytes();
        } catch (SpelEvaluationException e) {
            String errorMessage = String.format("Couldn't extract key from request using key expression '%s'",
                    keyExpression);
            log.warn(errorMessage, e);
            throw new RuntimeException(errorMessage);
        }
    }

    @ConditionalOnProperty(value = HttpSourcePojoProperties.HTTP_POJO_KEY_EXPRESSION, matchIfMissing = true)
    @Bean(POJO_KEY_EXTRACTOR_BEAN_NAME)
    Consumer<HeaderEnricherSpec> defaultPojoKeyExtractor() {
        return e -> e.headerFunction(Headers.KEY_BYTES, (message) -> null);
    }
}
