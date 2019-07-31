package com.github.wpik.httpsource;

import com.github.wpik.httpsource.json.JsonPathKeyExtractorConfig;
import com.github.wpik.httpsource.json.JsonSchemaValidatorConfig;
import com.github.wpik.httpsource.pojo.PojoDeserializerConfig;
import com.github.wpik.httpsource.pojo.PojoKeyExtractorConfig;
import com.github.wpik.httpsource.pojo.PojoValidatorConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.http.dsl.Http;

import java.util.function.Consumer;

@SpringBootApplication
@EnableConfigurationProperties({HttpSourceProperties.class})
@EnableBinding(Source.class)
public class HttpSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpSourceApplication.class, args);
    }

    @Bean
    public IntegrationFlow httpInbound(
            HttpSourceProperties properties,
            @Qualifier(PojoDeserializerConfig.POJO_DESERIALIZER_BEAN_NAME)
                    Consumer<HeaderEnricherSpec> pojoDeserializer,
            @Qualifier(PojoValidatorConfig.POJO_VALIDATOR_BEAN_NAME)
                    GenericHandler<?> pojoValidator,
            @Qualifier(PojoKeyExtractorConfig.POJO_KEY_EXTRACTOR_BEAN_NAME)
                    Consumer<HeaderEnricherSpec> pojoKeyExtractor,
            @Qualifier(JsonSchemaValidatorConfig.JSON_SCHEMA_VALIDATOR_BEAN_NAME)
                    GenericHandler<?> jsonValidator,
            @Qualifier(JsonPathKeyExtractorConfig.JSON_PATH_KEY_EXTRACTOR_BEAN_NAME)
                    Consumer<HeaderEnricherSpec> jsonPathKeyExtractor
            ) {
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
                .handle(jsonValidator)
                .enrichHeaders(pojoDeserializer)
                .handle(pojoValidator)
                .enrichHeaders(jsonPathKeyExtractor)
                .enrichHeaders(pojoKeyExtractor)
                .channel(Source.OUTPUT)
                .get();
    }
}
