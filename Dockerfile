FROM openjdk:8-jdk-alpine

ADD . .

RUN ./gradlew clean build

ENTRYPOINT ["java", "-jar", "build/libs/ot-demo-0.0.1-SNAPSHOT.jar"]