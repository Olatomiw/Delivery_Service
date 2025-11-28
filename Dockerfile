FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy Maven files for dependency resolution
COPY pom.xml .

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:resolve -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Create final image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the built jar
COPY --from=0 /app/target/delivery-window-service-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]