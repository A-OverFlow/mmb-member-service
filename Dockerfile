FROM amazoncorretto:21

ARG JAR_FILE=build/libs/mmb-member-service-1.0.0.jar

COPY ${JAR_FILE} member-service.jar

ENTRYPOINT ["java","-jar","/member-service.jar"]