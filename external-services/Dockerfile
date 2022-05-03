FROM openjdk:11-jre-slim

WORKDIR application

ADD external-services-v1.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/application/app.jar"]
