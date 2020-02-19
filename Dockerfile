
FROM adoptopenjdk:11-jre-hotspot
COPY * .
RUN ./gradlew bootJar
COPY build/libs/TurntablAPIGateway-0.0.1-SNAPSHOT.jar   app.jar
RUN chmod 777 app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]