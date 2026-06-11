FROM eclipse-temurin:17-jre

WORKDIR /app

ARG JAR_FILE=target/*.jar
ENV JAVA_OPTS=""

COPY ${JAR_FILE} app.jar

EXPOSE 8761

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
