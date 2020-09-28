FROM openjdk:15-slim-buster

ENV TZ=Europe/Berlin

RUN mkdir /app
WORKDIR /app

RUN useradd --no-log-init --no-create-home --shell /bin/false service_user

# Arguments
ARG SOURCE_JAR_FILE="webapp/target/*.jar"
ARG BUILD_DATE
ARG VCS_REF

# Labels
LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.build-date=$BUILD_DATE
LABEL org.label-schema.name="metaldetector/metal-detector"
LABEL org.label-schema.description="Mark your favorite metal bands and receive regular email alerts about upcoming and recently released albums from your favorite bands."
LABEL org.label-schema.maintainer="https://github.com/MetalDetectorRocks"
LABEL org.label-schema.url="https://metal-detector.rocks"
LABEL org.label-schema.vcs-url="https://github.com/MetalDetectorRocks/metal-detector-main"
LABEL org.label-schema.vcs-ref=$VCS_REF

COPY $SOURCE_JAR_FILE app.jar

RUN sh -c "touch app.jar"

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx256m", "-jar", "app.jar"]
