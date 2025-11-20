FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/InvoiceGenerator-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

CMD [ "java", "-jar", "app.jar" ]