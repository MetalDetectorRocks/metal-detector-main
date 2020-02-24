FROM openjdk:11-stretch

ARG BUILD_DATE
LABEL org.label-schema.build-date=$BUILD_DATE

COPY target/metal-detector-0.0.1.jar metal-detector.jar

RUN sh -c 'touch /metal-detector.jar'

EXPOSE 8090

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/metal-detector.jar"]
