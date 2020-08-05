FROM openjdk:8
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY target/gcp-poc1-282308-ac32bfcf3643.json gcp-poc1-282308-ac32bfcf3643.json
ENTRYPOINT ["java","-jar","/app.jar"]
 