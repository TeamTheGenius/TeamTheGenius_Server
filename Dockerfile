# Start with a base image containing Java runtime.
FROM openjdk:17-jdk

# The application's jar file.
ARG JAR_FILE=./build/libs/GitGetApplication.jar

# Add the application's jar to the container.
COPY ${JAR_FILE} App.jar

# Run the jar file.
CMD ["java", "-jar", "App.jar"]