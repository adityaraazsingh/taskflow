FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Activates application-prod.properties. Requires JWT_SECRET to be set in the environment;
# set DB_PATH to a mounted persistent disk (e.g. /var/data/taskflow/db) to keep data across deploys.
ENV SPRING_PROFILES_ACTIVE=prod
RUN mkdir -p /var/data/taskflow

CMD ["java", "-jar", "app.jar"]