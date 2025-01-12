#FIRST STAGE
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# copy pom.xml and install dependencies first to leverage the cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy the rest of the application and build it
COPY src /app/src
RUN mvn clean package -DskipTests -B


# SECOND STAGE: Create the final image with the JAR
FROM eclipse-temurin:21-jdk-alpine

COPY --from=build /app/target/*.jar /app/eurekaServer.jar
ENTRYPOINT ["java", "-jar", "/app/eurekaServer.jar"]


