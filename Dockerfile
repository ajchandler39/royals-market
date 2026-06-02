# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline
COPY src ./src
RUN ./mvnw -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre AS run
WORKDIR /app
COPY --from=build /app/target/royalsmarket-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Default to the Postgres-backed prod profile; override with -e SPRING_PROFILES_ACTIVE=default for H2.
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
