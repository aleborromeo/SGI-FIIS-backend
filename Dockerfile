FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar archivos del proyecto Spring
COPY fiis/pom.xml .
COPY fiis/src ./src

# Construir el proyecto
RUN mvn clean package -DskipTests

# Imagen final ligera
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]