# First Stage: Build with Maven
FROM maven:3.8.4-openjdk-11 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean install

# Second Stage: Create the final image
FROM openjdk:8-jdk-alpine
COPY --from=builder /app/target/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
