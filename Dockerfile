# Start with a base image containing Java runtime.
FROM openjdk:17-jdk

# The application's jar file.
ARG JAR_FILE=./TeamTheGenius_Server/build/libs/gitget-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container.
COPY ${JAR_FILE} app.jar

# Run the jar file.
CMD ["java", "-jar", "app.jar"]