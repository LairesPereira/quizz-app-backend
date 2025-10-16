# Build
FROM maven:3.9.3-eclipse-temurin-20 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:20-jdk AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
