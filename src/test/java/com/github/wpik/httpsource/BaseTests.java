package com.github.wpik.httpsource;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseTests {
    @Autowired
    protected Source channels;

    @Autowired
    protected MessageCollector messageCollector;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected ParameterizedTypeReference badRequestResponseType = new ParameterizedTypeReference<Map<String, String>>() {
    };

    protected String readTestFileAsString(String classPathName) throws IOException {
        try (InputStream stream = this.getClass().getResourceAsStream(classPathName)) {
            return IOUtils.toString(stream, Charset.defaultCharset());
        }
    }
}
