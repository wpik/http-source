package com.github.wpik.httpsource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("http")
@Validated
@Data
public class HttpSourceProperties {

    /**
     * URI path where requests should be handled.
     * By default: /
     */
    @NotEmpty
    private String uriPath = "/";

    /**
     * Array of HTTP methods accepted by the source.
     * By default: POST
     */
    @NotEmpty
    private HttpMethod[] httpMethods = {HttpMethod.POST};

    /**
     * Array of patterns to map HTTP headers into message headers.
     * By default all common headers (see DefaultHttpHeaderMapper).
     */
    private String[] mappedRequestHeaders = {DefaultHttpHeaderMapper.HTTP_REQUEST_HEADER_NAME_PATTERN};

    /**
     * The HTTP response status returned to the client, after successful processing of HTTP request.
     * By default: ACCEPTED
     */
    @NotNull
    private HttpStatus responseStatus = HttpStatus.ACCEPTED;

    @NestedConfigurationProperty
    private HttpSourceCorsProperties cors = new HttpSourceCorsProperties();

    @NestedConfigurationProperty
    private HttpSourcePojoProperties pojo = new HttpSourcePojoProperties();
}
