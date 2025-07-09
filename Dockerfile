FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Maven wrapper and pom files
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
COPY model/pom.xml model/
COPY core/pom.xml core/
COPY dto/pom.xml dto/
COPY server/pom.xml server/

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY . .

# Build the application
RUN ./mvnw clean install -DskipTests

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "server/target/server-1.0-SNAPSHOT.jar"]
