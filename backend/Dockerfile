FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/chuchenblog-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

VOLUME /app/uploads

ENTRYPOINT ["java", "-jar", "app.jar"]