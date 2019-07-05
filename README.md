# http-source

Spring cloud streams HTTP source that validates payload against JSON schema or POJO class and sends it to output stream. Partitioning is supported.


## Configuring topic name

You can define Kafka topic this way:

```yaml
spring:
  cloud:
    stream:
      bindings:
        output:
          destination: foobar
```



## Return 500 on Kafka unavailability

In order to return HTTP 500 when Kafka is unavailable, enable synchronous Kafka binder:

```yaml
spring:
  cloud:
    stream:
      kafka:
        bindings:
          output:
            producer:
              sync: true
```