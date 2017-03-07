FROM maven:3-jdk-8

RUN apt-get update && apt-get install -y --no-install-recommends graphviz && rm -rf /var/lib/apt/lists/*
ADD . /app
WORKDIR /app
EXPOSE 8080
CMD ["mvn", "jetty:run"]
