package com.github.wpik.httpsource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("http")
@Validated
@Data
public class HttpSourceProperties {

    @NotEmpty
    private String uriPath = "/";

    @NotEmpty
    private HttpMethod[] httpMethods = {HttpMethod.POST};

    private String[] mappedRequestHeaders = {DefaultHttpHeaderMapper.HTTP_REQUEST_HEADER_NAME_PATTERN};

    @NotNull
    private HttpStatus responseStatus = HttpStatus.ACCEPTED;

    private CorsProperties cors = new CorsProperties();

    @Data
    public static class CorsProperties {
        private String[] allowedOrigins = {CorsConfiguration.ALL};
        private String[] allowedHeaders = {CorsConfiguration.ALL};
        private Boolean allowCredentials;
    }
}
