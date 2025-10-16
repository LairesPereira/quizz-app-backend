# Imagem base leve do OpenJDK 17
FROM eclipse-temurin:17-jdk-alpine

# Diretório de trabalho
WORKDIR /app

# Copia o JAR gerado pelo Maven/Gradle
COPY target/*.jar app.jar

# Expõe a porta que o Spring Boot vai rodar
EXPOSE 8080

# Comando de start
ENTRYPOINT ["java","-jar","app.jar"]
