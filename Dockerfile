# Etapa 1 — Build
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2 — Runtime leve
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o jar gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Porta exposta
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

