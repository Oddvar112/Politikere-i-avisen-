FROM amazoncorretto:17-alpine AS build

# Install Maven
RUN apk add --no-cache maven

WORKDIR /app

# Copy pom files first for better caching
COPY pom.xml .
COPY model/pom.xml model/
COPY core/pom.xml core/
COPY dto/pom.xml dto/
COPY server/pom.xml server/

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY . .

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM amazoncorretto:17-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/server/target/server-1.0-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
