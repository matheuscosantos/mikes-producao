FROM openjdk:21-jdk-alpine

WORKDIR /app

COPY build/libs/mikes-producao.jar mikes-producao.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "mikes-producao.jar"]