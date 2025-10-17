# Etapa 1 — Build
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o arquivo de configuração e as dependências primeiro (melhor cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte
COPY src ./src

# Compila o projeto (gera o JAR)
RUN mvn clean package -DskipTests

# Etapa 2 — Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o JAR da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta (caso o Fly.io precise mapear)
EXPOSE 8080

# Comando para rodar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
