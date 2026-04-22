# ─── Build Stage ───────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build

# Cache dependencies before copying source
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q

COPY src/ src/
RUN ./mvnw package -DskipTests -q

# ─── Runtime Stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /build/target/rent-tech-api-*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 6002

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
