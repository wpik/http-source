package com.github.wpik.httpsource.json;

import com.github.wpik.httpsource.HttpSourceApplication;
import com.github.wpik.httpsource.HttpSourceJsonProperties;
import com.github.wpik.httpsource.HttpSourceProperties;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;

@Configuration
public class JsonSchemaValidatorConfig {

    public static final String JSON_SCHEMA_VALIDATOR_BEAN_NAME = "jsonSchemaValidator";

    @ConditionalOnProperty(HttpSourceJsonProperties.HTTP_JSON_SCHEMA_LOCATION)
    @Bean
    Schema jsonSchema(HttpSourceProperties httpSourceProperties) {
        String schemaLocation = httpSourceProperties.getJson().getSchemaLocation();
        InputStream schemaStream = HttpSourceApplication.class.getResourceAsStream(schemaLocation);
        JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStream));
        return SchemaLoader.load(jsonSchema);
    }

    @ConditionalOnProperty(HttpSourceJsonProperties.HTTP_JSON_SCHEMA_LOCATION)
    @Bean(JSON_SCHEMA_VALIDATOR_BEAN_NAME)
    GenericHandler<String> jsonValidator(Schema jsonSchema) {
        return (payload, headers) -> {
            try {
                jsonSchema.validate(new JSONObject(new JSONTokener(payload)));
                return payload;
            } catch (ValidationException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", e.getAllMessages()));
            }
        };
    }

    @ConditionalOnProperty(value = HttpSourceJsonProperties.HTTP_JSON_SCHEMA_LOCATION, matchIfMissing = true)
    @Bean(JSON_SCHEMA_VALIDATOR_BEAN_NAME)
    GenericHandler<?> defaultJsonValidator() {
        return (payload, headers) -> payload;
    }
}
