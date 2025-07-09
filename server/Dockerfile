FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR file from the parent build context
COPY target/server-1.0-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
