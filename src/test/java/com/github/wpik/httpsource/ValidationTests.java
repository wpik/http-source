package com.github.wpik.httpsource;

import org.junit.Test;
import org.springframework.http.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class ValidationTests {

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.pojo.class-name=com.github.wpik.httpsource.model.Person"
    })
    public static class PojoValidationTests extends BaseTests {
        @Test
        public void testPayloadPassValidation() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
        }

        @Test
        public void testPayloadFailsValidationWrongAge() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-age.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("age"));
        }

        @Test
        public void testPayloadFailsValidationWrongAddress() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-address.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("address"));
        }

        @Test
        public void testPayloadFailsValidationWrongAgeAndAddress() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-age-address.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("age"));
            assertTrue(response.getBody().get("message").contains("address"));
        }

        @Test
        public void testPayloadFailsPojoVerificationExtraKey() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-extra-key.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("nationality"));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.json.schema-location=/schema/person-schema.json"
    })
    public static class JsonSchemaValidationTests extends BaseTests {
        @Test
        public void testPayloadPassValidation() throws IOException, URISyntaxException, InterruptedException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<?> response = restTemplate.exchange(request, Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Message<?> message = messageCollector.forChannel(channels.output()).poll(1, TimeUnit.SECONDS);
            assertEquals(json, message.getPayload());
            assertEquals(MediaType.APPLICATION_JSON, message.getHeaders().get(MessageHeaders.CONTENT_TYPE));
        }

        @Test
        public void testPayloadFailsValidationWrongAge() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-age.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("age"));
        }

        @Test
        public void testPayloadFailsValidationWrongAddress() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-address.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("address"));
        }

        @Test
        public void testPayloadFailsValidationWrongAgeAndAddress() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-age-address.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("age"));
            assertTrue(response.getBody().get("message").contains("address"));
        }

        @Test
        public void testPayloadFailsValidationExtraKey() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/invalidPerson-extra-key.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("extraneous key"));
            assertTrue(response.getBody().get("message").contains("nationality"));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.pojo.class-name=com.github.wpik.httpsource.model.Person",
            "http.json.schema-location=/schema/fish-schema.json"
    })
    public static class MixedValidationTestsJsonFails extends BaseTests {
        @Test
        public void testJsonValidationBeforePojo() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("species"));
        }
    }

    @TestPropertySource(properties = {
            "http.uri-path=/foo",
            "http.pojo.class-name=com.github.wpik.httpsource.model.Car",
            "http.json.schema-location=/schema/person-schema.json"
    })
    public static class MixedValidationTestsPojoFails extends BaseTests {
        @Test
        public void testJsonValidationBeforePojo() throws IOException, URISyntaxException {
            String json = readTestFileAsString("/testdata/validPerson.json");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            RequestEntity<String> request = new RequestEntity<>(json, headers, HttpMethod.POST, new URI("/foo"));
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(request, badRequestResponseType);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().get("message").contains("firstname"));
        }
    }
}
