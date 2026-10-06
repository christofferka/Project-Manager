# Multi-stage build: bygger JAR i en container med JDK,
# koerer derefter JAR i en mindre container med kun JRE.

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Kopier Maven wrapper og pom først (udnytter Docker cache)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Kopier resten og byg
COPY src/ src/
RUN ./mvnw -B -DskipTests clean package

# ---- Runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render saetter PORT dynamisk; demo-profilen bruger den via server.port=${PORT:8080}
ENV SPRING_PROFILES_ACTIVE=demo
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
