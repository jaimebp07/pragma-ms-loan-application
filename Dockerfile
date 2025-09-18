# Etapa de construcción
FROM gradle:8.14.3-jdk17 AS build
WORKDIR /home/gradle/project
COPY . .
#RUN gradle :applications:app-service:clean :applications:app-service:build -x test
RUN gradle :applications:app-service:build -x test

# Etapa de ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/project/applications/app-service/build/libs/app-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]