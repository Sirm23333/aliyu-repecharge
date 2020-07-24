FROM openjdk:8-jre-alpine

ARG port=10600
ENV SERVICE_PORT=${port}
EXPOSE ${port}
COPY target/MiniFass-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
