FROM openjdk:8-jdk-slim
LABEL maintainer="Laurentiu Marcut"

COPY target/*.jar /app.jar
EXPOSE 8080

CMD ["java", "-Xms256m","-Xmx256m", "-jar", "/app.jar"]