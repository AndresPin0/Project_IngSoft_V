FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
COPY logs /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
