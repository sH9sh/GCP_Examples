FROM maven:3.8.5-openjdk-17-slim
WORKDIR /GCPexamples
COPY src     /GCPexamples/src
COPY pom.xml /GCPexamples
RUN mvn clean package

FROM openjdk:17-jdk-alpine
COPY --from=0 /GCPexamples/target/GCPexamples-0.0.1-SNAPSHOT.jar  /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]