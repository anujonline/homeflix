FROM maven:3.8.3-openjdk-17 AS build
COPY / /src
RUN mvn -f /src/pom.xml clean package -Pproduction
FROM eclipse-temurin:17-jre
COPY --from=build /src/target/*.jar app.jar
RUN rm -fr /src
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "/app.jar"]