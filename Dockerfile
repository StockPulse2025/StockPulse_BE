FROM openjdk:17-jdk-slim

COPY myapp.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]