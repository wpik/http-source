
# Running with Spring Cloud Data Flow

## Starting Spring Cloud Data Flow

```bash
cd .dataflow
. versions
docker-compose up -d
```

## Running shell

```bash
docker-compose exec dataflow-server java -jar shell.jar
```

### Services

- Spring Cloud Data Flow Dashboard - http://localhost:9393/dashboard
- Prometheus - http://localhost:9090

## Sample use case

Below we will build, deploy and create simple stream that writes HTTP requests to the file.

1. Build

    ```bash
    mvn clean package -f ../pom.xml
    ```

    NOTE: `target` directory is mapped under `/target` on `dataflow-server` service in `docker-compose.yml`.

2. Register HTTP source

    - using shell
        ```bash
        docker-compose exec dataflow-server java -jar shell.jar
        ```
        
        ```
        app register --name http-pro --type source --uri "file:///target/http-source-0.0.1-SNAPSHOT.jar"
        ```
    
    - using REST API
        
        ```bash
        curl 'http://localhost:9393/apps/source/http-pro' -i -X POST -d 'uri=file%3A%2F%2F%2Ftarget%2Fhttp-source-0.0.1-SNAPSHOT.jar'
        ```
        
3. Define stream

    We will define stream from HTTP Source to the file: `http-to-file`
  
    - using shell
    
        ```bash
        stream create --definition "http-pro --port=8090 | file --directory=/tmp --name=http-requests" --name http-to-file
        ```

4. Validate stream

    - using shell
    
        ```bash
        stream validate --name http-to-file
        ```

5. Deploy stream

    - using shell
    
        ```bash
        stream deploy --name http-to-file
        ```

6. Get stream details

    - using shell
    
        ```bash
        stream info --name http-to-file
        ```

7. Test stream

    - generate request
    
        ```bash
        docker-compose exec skipper-server curl -H 'Content-Type: application/json' -d '{"name":"john"}' http://localhost:8090
        ```

    - monitor file
    
        ```bash
        docker-compose exec skipper-server tail -f /tmp/http-requests
        ``` 
    
    - consume kafka topic
    
        ```bash
        docker-compose exec kafka kafka-topics --bootstrap-server=kafka:9092 --list
        docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --topic http-to-file.http-pro
        ```

8. Undeploy stream

    - using shell
    
        ```bash
        stream undeploy --name http-to-file
        ```

9. Destroy stream

    - using shell
    
        ```bash
        stream destroy --name http-to-file
        ```

10. Unregister Http source

    - using shell
    
        ```bash
        app unregister --type source --name http-pro
        ```
        
    - using REST API
        
        ```bash
        curl 'http://localhost:9393/apps/source/http-pro' -i -X DELETE
        ```

## Troubleshooting

Browse skipper logs for executed command:

`docker-compose logs skipper-server | grep 'Command to be executed:'`

Execute command manualy: 

`docker-compose exec skipper-server <command>`
