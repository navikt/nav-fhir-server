FROM gcr.io/distroless/java21-debian12@sha256:bce78ee52879b0dcbecd23f8b6a33c5ee98943f6810f117e95aa69e14adb22ba

WORKDIR /app

COPY build/libs/*.jar app.jar

ENV TZ="Europe/Oslo"
ENV JAVA_OPTS="-Dlogback.configurationFile=logback.xml"

EXPOSE 9001

USER nonroot

CMD ["app.jar"]
