FROM gradle:8.2-jdk17 as build
COPY ../ /usr/kokline
WORKDIR /usr/kokline
RUN gradle backend:build -x test

FROM openjdk:17-alpine3.14
COPY --from=build /usr/kokline/backend/build/libs/*.jar /usr/app/App.jar
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "App.jar"]
