FROM azul/zulu-openjdk-alpine:21 AS builder

WORKDIR app

COPY /src src
COPY mvnw .
COPY .mvn .mvn
COPY /pom.xml pom.xml

RUN ./mvnw package

FROM azul/zulu-openjdk-alpine:21-jre-headless AS runner

COPY --from=builder /app/target/*.jar /app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=compose", "-jar", "app.jar"]