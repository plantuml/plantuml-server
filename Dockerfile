FROM maven:3-jdk-8

ADD . /app
WORKDIR /app
EXPOSE 8080
CMD ["mvn", "jetty:run"]
