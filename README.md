# http-source

Spring cloud streams HTTP source that validates payload against JSON schema or POJO and sends it to output stream.
Partitioning is supported.


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



## Payload validation

### Validation against POJO

To validate incoming payload against POJO, add it to the classpath and put its full name into configuration.

```yaml
http:
  pojo:
    class-name: <full class name, e.g. some.package.Person>
```

The POJO can use `javax.validation` annotations. For samples check 
[sample model in tests](/src/test/java/com/github/wpik/httpsource/model).
 


## Message partitioning

Messages can be partitioned in two ways:

1. By Spring Cloud Streams
2. By Kafka itself



### Partitioning by Spring Cloud Streams

In this scenario, Spring Cloud Stream based on the message key, calculates the partition to which the message should 
be published. Put the following configuration into `application.yml` file:

```yaml
spring:
  cloud:
    stream:
      bindings:
        output:
          producer:
            partitionCount: <...>
            partitionKeyExpression: <...>
```

Where:
  - `partitionCount` - defines the number of partitions for topic
  - `partitionKeyExpression` - defines the *SpEL* expression used to calculate the key that will be used to 
                               partition the message, e.g. `payload.id` will use `id` fiels from request body 
  - check [Spring Cloud Stream reference](https://cloud.spring.io/spring-cloud-stream/spring-cloud-stream.html#_producer_properties)
    for details and other partitioning options



### Partitioning by Kafka

In this scenario, each message has assigned key, based on which Kafka will calculate the partition, to which the 
message should be published.

The key is calculated by the service based on the POJO class. The extracted key is stored in the `keyBytes` header 
which can be used in Kafka Binder configuration:

```yaml
spring:
  cloud:
    stream:
      kafka:
        bindings:
          output:
            producer:
              messageKeyExpression: headers['keyBytes']
```


To extract key using POJO class, first you need to define the POJO class using `http.pojo.class-name` property.
Then you define the SpEL expression used to calculate the key. The base for the SpEL expression is the request body 
converted to the POJO class.

```yaml
http:
  pojo:
    key-expression: <...>
```

