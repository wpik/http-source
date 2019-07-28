package com.github.wpik.httpsource.pojo;

import com.github.wpik.httpsource.Headers;
import com.github.wpik.httpsource.HttpSourcePojoProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class PojoValidatorConfig {
    public static final String POJO_VALIDATOR_BEAN_NAME = "pojoValidator";

    @ConditionalOnProperty(HttpSourcePojoProperties.HTTP_POJO_CLASS_NAME)
    @Bean(POJO_VALIDATOR_BEAN_NAME)
    GenericHandler<?> pojoValidator(Validator validator) {
        return (payload, headers) -> {
            Object deserialized = headers.get(Headers.DESERIALIZED_OBJECT);
            if (deserialized != null) {
                Set<ConstraintViolation<Object>> violations = validator.validate(deserialized);
                if (violations.size() > 0) {
                    List<String> errors = violations
                            .stream()
                            .map(v -> v.getPropertyPath().toString() + ": " + v.getMessage())
                            .collect(Collectors.toList());

                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errors));
                }
            }
            return payload;
        };
    }

    @ConditionalOnProperty(value = HttpSourcePojoProperties.HTTP_POJO_CLASS_NAME, matchIfMissing = true)
    @Bean(POJO_VALIDATOR_BEAN_NAME)
    GenericHandler<?> defaultPojoValidator() {
        return (payload, headers) -> payload;
    }
}
