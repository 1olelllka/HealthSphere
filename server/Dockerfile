FROM eclipse-temurin:22-jdk-alpine as build

RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]