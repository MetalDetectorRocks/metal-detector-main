FROM openjdk:11-stretch

ARG BUILD_DATE
LABEL org.label-schema.build-date=$BUILD_DATE

COPY target/metal-release-radar-0.0.1.jar metal-release-radar.jar

RUN sh -c 'touch /metal-release-radar.jar'

EXPOSE 8090

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/metal-release-radar.jar"]
