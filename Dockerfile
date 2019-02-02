FROM openjdk:11-jre-slim

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/reservations/reservations.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD target/lib           /usr/share/reservations/lib
# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/reservations/reservations.jar