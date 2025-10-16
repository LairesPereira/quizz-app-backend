# Etapa de build
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Diretório de trabalho
WORKDIR /app

# Copia pom.xml e arquivos de configuração primeiro (para cache do Maven)
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o JAR (skip dos testes para acelerar)
RUN mvn clean package -DskipTests

# Etapa de runtime
FROM eclipse-temurin:17-jdk-alpine

# Diretório de trabalho
WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que o Spring Boot vai rodar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]
