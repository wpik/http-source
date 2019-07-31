package com.github.wpik.httpsource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(HttpSourceJsonProperties.PREFIX)
@Validated
@Data
public class HttpSourceJsonProperties {

    static final String PREFIX = "http.json";

    public static final String HTTP_JSON_SCHEMA_LOCATION = PREFIX + ".schema-location";
    public static final String HTTP_JSON_KEY_EXPRESSION = PREFIX + ".key-expression";

    /**
     * Classpath location of the JSON Schema to be used to validate the HTTP request body.
     */
    private String schemaLocation;

    /**
     * JSON Path expression used to extract key from the HTTP request body.
     */
    private String keyExpression;
}
