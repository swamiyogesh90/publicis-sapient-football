FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /opt/app

ARG JAR_FILE=target/*.jar

COPY --from=build /home/app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]