FROM maven:3.6-jdk-8 AS builderjetty

COPY pom.xml /app/
COPY src /app/src/

WORKDIR /app
RUN mvn --batch-mode --define java.net.useSystemProxies=true package

########################################################################################

FROM jetty:9.4-jre8
MAINTAINER D.Ducatel

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends graphviz fonts-noto-cjk && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

USER jetty

ENV GRAPHVIZ_DOT=/usr/bin/dot

ARG BASE_URL=ROOT
COPY --from=builderjetty /app/target/plantuml.war /var/lib/jetty/webapps/$BASE_URL.war
