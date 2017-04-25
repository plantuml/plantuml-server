FROM jetty
MAINTAINER D.Ducatel

ADD target/plantuml.war /var/lib/jetty/webapps/ROOT.war
