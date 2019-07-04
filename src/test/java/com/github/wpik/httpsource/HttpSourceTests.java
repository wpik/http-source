package com.github.wpik.httpsource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.integration.test.matcher.HeaderMatcher.hasHeader;
import static org.springframework.integration.test.matcher.PayloadMatcher.hasPayload;

public abstract class HttpSourceTests {

    @TestPropertySource(properties = {"http.uriPath = /foo"})
    public static class NonSecuredTests extends BaseTests {

        @Test
        public void testText() {
            ResponseEntity<?> entity = this.restTemplate.postForEntity("/foo", "hello", Object.class);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity.getStatusCode());
        }

        @Test
        public void testJson() throws Exception {
            String json = "{\"foo\":1,\"bar\":true}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("foo", "bar");
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON_UTF8, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertFalse(message.getHeaders().containsKey("foo"));
        }

        @Test
        @SuppressWarnings("rawtypes")
        public void testHealthEndpoint() {
            ResponseEntity<Map> response = this.restTemplate.getForEntity("/actuator/health", Map.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.hasBody());

            Map health = response.getBody();

            assertEquals("UP", health.get("status"));
        }

        @Test
        @SuppressWarnings("rawtypes")
        public void testEnvEndpoint() {
            ResponseEntity<Map> response = this.restTemplate.getForEntity("/actuator/env", Map.class);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }


    @TestPropertySource(properties = {
            "http.uriPath = /foo",
            "http.mappedRequestHeaders = f*"
    })
    public static class HeaderTests extends BaseTests {

        @Test
        public void headersAreMapped() throws Exception {
            String json = "{\"foo\":1,\"bar\":true}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("foo", "bar");
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertTrue(message.getHeaders().containsKey("foo"));
        }
    }


    @TestPropertySource(properties = "management.endpoints.web.exposure.include = *")
    public static class NonSecuredManagementDisabledTests extends BaseTests {

        @Test
        public void testText() {
            ResponseEntity<?> entity = this.restTemplate.postForEntity("/", "hello", Object.class);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, entity.getStatusCode());
        }

        @Test
        public void testJson() throws URISyntaxException {
            String json = "{\"foo\":1,\"bar\":true}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/"));
            ResponseEntity<?> entity = this.restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, entity.getStatusCode());
            assertThat(messageCollector.forChannel(channels.output()), receivesPayloadThat(is(json)));
        }

        @Test
        @SuppressWarnings("rawtypes")
        public void testHealthEndpoint() {
            ResponseEntity<Map> response = this.restTemplate.getForEntity("/actuator/health", Map.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.hasBody());

            Map health = response.getBody();

            assertEquals("UP", health.get("status"));
        }

        @Test
        public void testEnvEndpoint() {
            ResponseEntity<Object> response = this.restTemplate.getForEntity("/actuator/env", Object.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.hasBody());
        }

    }


    @TestPropertySource(properties = {
            "http.mappedRequestHeaders = *",
            "spring.cloud.streamapp.security.enabled = true",
            "spring.cloud.streamapp.security.csrf-enabled=false",
            "http.cors.allowedOrigins = /bar"
    })
    public static class SecuredTests extends BaseTests {

        @Autowired
        private SecurityProperties securityProperties;

        @Before
        public void setup() {
            this.restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(
                    securityProperties.getUser().getName(), securityProperties.getUser().getPassword()));
        }

        @Test
        public void testJson() throws Exception {
            String json = "{\"foo\":1,\"bar\":true}";
            HttpHeaders headers = new HttpHeaders();
            headers.set("foo", "bar");
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.ORIGIN, "/bar");
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/"));
            ResponseEntity<?> response = this.restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertThat(message, hasPayload(json));
            assertThat(message, hasHeader("foo", "bar"));

            headers.set(HttpHeaders.ORIGIN, "/junk");
            request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/"));
            response = this.restTemplate.exchange(request, String.class);
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertEquals("Invalid CORS request", response.getBody());
        }

        @Test
        @SuppressWarnings("rawtypes")
        public void testHealthEndpoint() {
            ResponseEntity<Map> response = this.restTemplate.getForEntity("/actuator/health", Map.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.hasBody());

            Map health = response.getBody();

            assertEquals("UP", health.get("status"));
        }

        @Test
        public void testEnvEndpoint() {
            ResponseEntity<Object> response = this.restTemplate.getForEntity("/actuator/env", Object.class);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

    }


    @TestPropertySource(properties = {
            "http.mappedRequestHeaders = *",
            "spring.cloud.streamapp.security.enabled = true",
            "spring.cloud.streamapp.security.csrf-enabled=true"
    })
    public static class CsrfEnabledTests extends BaseTests {

        @Autowired
        private SecurityProperties securityProperties;

        @Before
        public void setup() {
            this.restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(
                    securityProperties.getUser().getName(), securityProperties.getUser().getPassword()));
        }

        @Test
        public void testText() throws Exception {
            RequestEntity<String> request =
                    new RequestEntity<>("hello", new HttpHeaders(), HttpMethod.POST, new URI("/"));
            ResponseEntity<?> response = this.restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        @Test
        public void testJson() throws Exception {
            String json = "{\"foo\":1,\"bar\":true}";
            HttpHeaders headers = new HttpHeaders();
            headers.set("foo", "bar");
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.ORIGIN, "/bar");
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/"));
            ResponseEntity<?> response = this.restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }
}
