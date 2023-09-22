FROM gradle:8.2-jdk17 as build
COPY ./ /usr/backend
WORKDIR /usr/backend
RUN gradle build -x test

FROM openjdk:17-alpine3.14
COPY --from=build /usr/backend/build/libs/*.jar /usr/kokline/backend/KoklineBackend.jar
WORKDIR /usr/kokline/backend
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "KoklineBackend.jar"]
