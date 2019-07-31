package com.github.wpik.httpsource;

import org.junit.Test;
import org.springframework.http.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public abstract class KeyExtractingTests {

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.pojo.class-name=com.github.wpik.httpsource.model.Person",
            "http.pojo.key-expression=firstname"
    })
    public static class PojoKeyTests extends BaseTests {
        @Test
        public void testKeyIsExtracted() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertArrayEquals("jan".getBytes(), message.getHeaders().get(Headers.KEY_BYTES, byte[].class));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.pojo.class-name=com.github.wpik.httpsource.model.Person",
            "http.pojo.key-expression=age"
    })
    public static class PojoStringKeyTests extends BaseTests {
        @Test
        public void testKeyIsExtractedAsStringBytes() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertArrayEquals("20".getBytes(), message.getHeaders().get(Headers.KEY_BYTES, byte[].class));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.json.schema-location=/schema/person-schema.json",
            "http.json.key-expression=$.address.city"
    })
    public static class JsonKeyTests extends BaseTests {
        @Test
        public void testKeyIsExtracted() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertArrayEquals("warsaw".getBytes(), message.getHeaders().get(Headers.KEY_BYTES, byte[].class));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.json.schema-location=/schema/person-schema.json",
            "http.json.key-expression=$.age"
    })
    public static class JsonStringKeyTests extends BaseTests {
        @Test
        public void testKeyIsExtractedAsStringBytes() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertArrayEquals("20".getBytes(), message.getHeaders().get(Headers.KEY_BYTES, byte[].class));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo"
    })
    public static class NoKeyTests extends BaseTests {
        @Test
        public void testKeyIsExtracted() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertFalse(message.getHeaders().containsKey(Headers.KEY_BYTES));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.json.schema-location=/schema/person-schema.json",
            "http.json.key-expression=$.firstname",
            "http.pojo.class-name=com.github.wpik.httpsource.model.Person",
            "http.pojo.key-expression=address.city"
    })
    public static class MixedJsonPojoKeyTests extends BaseTests {
        @Test
        public void testJsonKeyHasPrecedence() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
            assertArrayEquals("jan".getBytes(), message.getHeaders().get(Headers.KEY_BYTES, byte[].class));
        }
    }
}
