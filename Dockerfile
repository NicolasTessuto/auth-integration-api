# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy gradle files for dependency resolution
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Give execution permissions to gradlew
RUN chmod +x gradlew

# Download dependencies (this layer will be cached)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN ./gradlew build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Environment variables for HubSpot integration
# These are placeholder values that should be overridden at runtime
# HubSpot Application credentials
ENV HUBSPOT_CLIENT_ID="xxxxx"
ENV HUBSPOT_CLIENT_SECRET="xxxxx"
ENV HUBSPOT_REDIRECT_URI="xxxxx"
ENV HUBSPOT_EXCHANGE_FOR_TOKEN_URL="xxxxx"
ENV RABBITMQ_USERNAME="xxxxx"
ENV RABBITMQ_PASSWORD="xxxxx"
ENV RABBIT_HUBSPOT_CONTACT_FALLBACK_QUEUE="xxxxx"

# HubSpot API configuration
ENV SPRING_PROFILES_ACTIVE="dev"
ENV SERVER_PORT="8080"
ENV LOGGING_LEVEL_ROOT="INFO"

# Create a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring

# Set proper Java options for containers
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set the ownership of the files to the non-root user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

