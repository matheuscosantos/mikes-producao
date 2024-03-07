FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

RUN apk update && apk add --no-cache curl

COPY build/libs/mikes-producao.jar mikes-producao.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "mikes-producao.jar"]