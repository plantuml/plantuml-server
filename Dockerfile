FROM jetty
MAINTAINER D.Ducatel

RUN apt-get update && \
    apt-get install -y --no-install-recommends graphviz fonts-wqy-zenhei && \
    rm -rf /var/lib/apt/lists/*

ADD target/plantuml.war /var/lib/jetty/webapps/ROOT.war
