package com.github.wpik.httpsource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(HttpSourcePojoProperties.PREFIX)
@Validated
@Data
public class HttpSourcePojoProperties {

    static final String PREFIX = "http.pojo";

    public static final String HTTP_POJO_CLASS_NAME = PREFIX + ".class-name";

    public static final String HTTP_POJO_KEY_EXPRESSION = PREFIX + ".key-expression";

    /**
     * Name of the class to be used to deserialize and validate body of HTTP requests.
     */
    private Class<?> className;

    /**
     * Expression evaluated against deserialized HTTP request to extract key. Requires http.pojo.className to be set.
     */
    private String keyExpression;
}
