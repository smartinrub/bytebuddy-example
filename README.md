# bytebuddy-example

## Installation

### Java Agent

```bash
mvn clean install
java -javaagent:advice-agent/target/advice-agent-1.0.jar -jar bytebuddy-client/target/bytebuddy-client-1.0.jar
```

### Running Tests

```bash
mvn test
```
