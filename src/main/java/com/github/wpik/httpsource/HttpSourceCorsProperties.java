package com.github.wpik.httpsource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

@ConfigurationProperties("http.cors")
@Validated
@Data
public class HttpSourceCorsProperties {
    /**
     * List of allowed origins.
     * By default ALL are allowed.
     */
    private String[] allowedOrigins = {CorsConfiguration.ALL};

    /**
     * List of allowed HTTP headers.
     * By default ALL are allowed.
     */
    private String[] allowedHeaders = {CorsConfiguration.ALL};

    /**
     * Whether the browser should include any cookies associated with the domain of the request being annotated.
     * By default: false
     */
    private Boolean allowCredentials;
}
