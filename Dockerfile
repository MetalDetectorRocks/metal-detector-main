FROM openjdk:15-slim-buster

ENV TZ=Europe/Berlin

RUN mkdir /app && mkdir /app/logs
WORKDIR /app

RUN useradd --no-log-init --no-create-home --shell /bin/false service_user
RUN chown -cR service_user:service_user /app
USER service_user

VOLUME ["/app/logs/"]

# Arguments
ARG SOURCE_JAR_FILE="webapp/target/*.jar"
ARG BUILD_DATE
ARG VCS_REF
ARG HEALTH_CHECK_ENDPOINT

# Labels
LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.build-date=$BUILD_DATE
LABEL org.label-schema.name="metaldetector/metal-detector"
LABEL org.label-schema.description="Mark your favorite metal bands and receive regular email alerts about upcoming and recently released albums from your favorite bands."
LABEL org.label-schema.maintainer="https://github.com/MetalDetectorRocks"
LABEL org.label-schema.url="https://metal-detector.rocks"
LABEL org.label-schema.vcs-url="https://github.com/MetalDetectorRocks/metal-detector-main"
LABEL org.label-schema.vcs-ref=$VCS_REF

HEALTHCHECK --start-period=10s --interval=10s --timeout=5s --retries=3 CMD curl --fail $HEALTH_CHECK_ENDPOINT || exit 1

COPY $SOURCE_JAR_FILE app.jar
COPY docker-entrypoint.sh /app

ENTRYPOINT ["/app/docker-entrypoint.sh"]
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx256m", "-jar", "app.jar"]
