FROM openjdk:8-jre-alpine

ARG port=10600
ENV SERVICE_PORT=${port}
EXPOSE ${port}
COPY target/MiniFass-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar
COPY src/main/java/com/aliyun/mini/scheduler/config/log4j.properties /app/log4j.properties
ENTRYPOINT ["java","-jar","-Dlog4j.configuration=file:/app/log4j.properties","/app/app.jar"]
