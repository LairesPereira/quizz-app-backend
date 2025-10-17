# Etapa 1 — Build da aplicação
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências primeiro (para cache eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2 — Runtime (imagem leve)
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o JAR gerado da etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
