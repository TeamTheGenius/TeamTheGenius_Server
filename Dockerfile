# Start with a base image containing Java runtime.
FROM openjdk:17

# The application's jar file.
ARG JAR_FILE= build/libs

# Add the application's jar to the container.
ADD ${JAR_FILE}/gitget-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file.
ENTRYPOINT ["java", "-jar", "app.jar"]