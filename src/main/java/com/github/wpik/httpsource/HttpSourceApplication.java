package com.github.wpik.httpsource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.http.dsl.Http;

@SpringBootApplication
@EnableConfigurationProperties({HttpSourceProperties.class})
@EnableBinding(Source.class)
public class HttpSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpSourceApplication.class, args);
    }

    @Bean
    public IntegrationFlow httpInbound(HttpSourceProperties properties) {
        return IntegrationFlows.from(
                Http.inboundChannelAdapter(properties.getUriPath())
                        .requestMapping(mapping ->
                                mapping
                                        .methods(properties.getHttpMethods())
                                        .consumes(MediaType.APPLICATION_JSON_VALUE))
                        .requestPayloadType(String.class)
                        .mappedRequestHeaders(properties.getMappedRequestHeaders())
                        .statusCodeExpression(new ValueExpression<>(properties.getResponseStatus()))
                        .crossOrigin(crossOrigin ->
                                crossOrigin
                                        .origin(properties.getCors().getAllowedOrigins())
                                        .allowedHeaders(properties.getCors().getAllowedHeaders())
                                        .allowCredentials(properties.getCors().getAllowCredentials())))
                .channel(Source.OUTPUT)
                .get();
    }
}
