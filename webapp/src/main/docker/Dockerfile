###
# Please note: The alpine base image is not available for platform linux/arm64.
###
FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine as builder

WORKDIR app

ARG SOURCE_JAR_FILE=application.jar
COPY ${SOURCE_JAR_FILE} app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine

WORKDIR app

ENV TZ=Europe/Berlin
EXPOSE 8080

RUN apk --no-cache add curl

ENV USER=service_user
ENV UID=12345
ENV GID=23456
RUN addgroup -g "$GID" "$USER" \
    && adduser \
    --disabled-password \
    --gecos "" \
    --home "$(pwd)" \
    --ingroup "$USER" \
    --no-create-home \
    --uid "$UID" \
    --shell /bin/false \
    "$USER" \
    && chown -cR $USER:$USER /app

USER $USER

ARG BUILD_DATE
ARG VCS_REF

LABEL org.label-schema.schema-version="1.0"
LABEL org.label-schema.build-date=$BUILD_DATE
LABEL org.label-schema.name="metaldetectorrocks/metal-detector"
LABEL org.label-schema.description="Mark your favorite metal bands and receive regular notifications about upcoming and recently released albums."
LABEL org.label-schema.maintainer="https://github.com/MetalDetectorRocks"
LABEL org.label-schema.url="https://metal-detector.rocks"
LABEL org.label-schema.vcs-url="https://github.com/MetalDetectorRocks/metal-detector-main"
LABEL org.label-schema.vcs-ref=$VCS_REF

COPY --from=builder app/dependencies/ ./
RUN true # Bug in older docker versions, see https://stackoverflow.com/questions/51115856/docker-failed-to-export-image-failed-to-create-image-failed-to-get-layer
COPY --from=builder app/spring-boot-loader/ ./
RUN true
COPY --from=builder app/snapshot-dependencies/ ./
RUN true
COPY --from=builder app/application/ ./
RUN true

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
