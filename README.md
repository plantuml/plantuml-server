# PlantUML Server

[![GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007](https://img.shields.io/github/license/plantuml/plantuml-server.svg?color=blue)](https://www.gnu.org/licenses/gpl-3.0)
[![latest tag](https://img.shields.io/github/v/tag/plantuml/plantuml-server)](https://github.com/plantuml/plantuml-server/tags)
![workflow status (Main)](https://github.com/plantuml/plantuml-server/actions/workflows/main.yml/badge.svg)
![workflow status (Tests)](https://github.com/plantuml/plantuml-server/actions/workflows/tests.yml/badge.svg)

[![online](https://img.shields.io/endpoint?url=https://www.plantuml.com/plantuml/badge)](https://www.plantuml.com/plantuml)
[![rate](https://img.shields.io/endpoint?url=https://www.plantuml.com/plantuml/rate)](https://www.plantuml.com/plantuml)
[![peak](https://img.shields.io/endpoint?url=https://www.plantuml.com/plantuml/rate?peak)](https://www.plantuml.com/plantuml)

[![GitHub Sponsors](https://img.shields.io/github/sponsors/plantuml?logo=github)](https://github.com/sponsors/plantuml/)
[![docker pulls](https://img.shields.io/docker/pulls/plantuml/plantuml-server.svg?color=blue)](https://hub.docker.com/r/plantuml/plantuml-server)
![Docker Image Size (Jetty)](https://img.shields.io/docker/image-size/plantuml/plantuml-server/jetty?label=jetty%20image%20size)
![Docker Image Size (Tomcat)](https://img.shields.io/docker/image-size/plantuml/plantuml-server/tomcat?label=tomcat%20image%20size)

PlantUML Server is a web application to generate UML diagrams on-the-fly.

[PlantUML is **not** affected by the log4j vulnerability.](https://github.com/plantuml/plantuml/issues/826)


![PlantUML Server](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/screenshot.png)

More examples and features about the Web UI can be found in [docs/WebUI](https://github.com/plantuml/plantuml-server/tree/master/docs/WebUI).

To know more about PlantUML, please visit https://plantuml.com.


## Requirements

- jre/jdk 11 or above
- apache maven 3.0.2 or above

## Recommendations

- Jetty 11 or above
- Tomcat 10 or above


## How to run the server

Just run:

```sh
mvn jetty:run
```

The server is now listening to [http://localhost:8080/plantuml](http://localhost:8080/plantuml).
In this way the server is run on an embedded jetty server.

You can specify the port at which it runs:

```sh
mvn jetty:run -Djetty.http.port=9999
```


## How to run the server with Docker

You can run Plantuml with jetty or tomcat container
```sh
docker run -d -p 8080:8080 plantuml/plantuml-server:jetty
docker run -d -p 8080:8080 plantuml/plantuml-server:tomcat
```

The server is now listening to [http://localhost:8080](http://localhost:8080).

### Read-only container

The jetty container supports read-only files system, you can run the read-only mode with:
```sh
docker run -d -p 8080:8080 --read-only -v /tmp/jetty plantuml/plantuml-server:jetty
```

This makes the container compatible with more restricted environment such as OpenShift, just make sure you mount a volume (can be ephemeral) on `/tmp/jetty`.

### Change base URL

To run plantuml using different base url, change the `docker-compose.yml` file:
```yaml
environment:
  - BASE_URL=plantuml
```

And run `docker-compose up`. This will start a modified version of the image using the base url `/plantuml`, e.g. http://localhost:8080/plantuml


## How to set PlantUML options

You can apply some option to your PlantUML server with environment variable.

If you run the directly the jar:
```sh
# NOTE: jetty-runner is deprecated.
# build war file and jetty-runner
mvn package
# start directly
# java $JVM_ARGS -jar jetty-runner.jar $JETTY_ARGS
java -jar target/dependency/jetty-runner.jar --config src/main/config/jetty.xml --port 9999 --path /plantuml target/plantuml.war
# see help for more possible options
java -jar target/dependency/jetty-runner.jar --help
```
Note: `--config src/main/config/jetty.xml` is only necessary if you need support for empty path segments in URLs (e.g. for the old proxy)

Alternatively, start over maven and pass the option with `-D` flag
```sh
mvn jetty:run -D THE_ENV_VARIABLE=THE_ENV_VALUE -Djetty.http.port=9999
```

If you use docker, you can use the `-e` flag:
```sh
docker run -d -p 9999:8080 -e THE_ENV_VARIABLE=THE_ENV_VALUE plantuml/plantuml-server:jetty
```

You can set all  the following variables:

* `BASE_URL`
  * PlantUML Base URL path
  * Default value: `ROOT`
* `PLANTUML_CONFIG_FILE`
  * Local path to a PlantUML configuration file (identical to the `-config` flag on the CLI)
  * Default value: `null`
* `PLANTUML_LIMIT_SIZE`
  * Limits image width and height
  * Default value: `4096`
* `PLANTUML_STATS`
  * Set it to `on` to enable [statistics report](https://plantuml.com/statistics-report)
  * Default value: `off`
* `HTTP_AUTHORIZATION`
  * when calling the `proxy` endpoint, the value of `HTTP_AUTHORIZATION` will be used to set the HTTP Authorization header
  * Default value: `null`
* `HTTP_PROXY_READ_TIMEOUT`
  * when calling the `proxy` endpoint, the value of `HTTP_PROXY_READ_TIMEOUT` will be the connection read timeout in milliseconds
  * Default value: `10000` (10 seconds)
* `ALLOW_PLANTUML_INCLUDE`
  * Enables `!include` processing which can read files from the server into diagrams. Files are read relative to the current working directory.
  * Default value: `false`


## Alternate: How to build your docker image

This method uses maven to run the application. That requires internet connectivity.
So, you can use following command to create a self-contained docker image that will "just work".

```sh
docker image build -f Dockerfile.jetty -t plantuml-server:local .
docker run -d -p 8080:8080 plantuml-server:local
```
The server is now listening to [http://localhost:8080](http://localhost:8080).

You may specify the port in `-p` Docker command line argument.


## How to generate the war

To build the war, just run:
```sh
mvn package
```
at the root directory of the project to produce plantuml.war in the target/ directory.

NOTE: If you want that the generated war includes the `apache-jsp` artifact run:
```sh
mvn package -Dapache-jsp.scope=compile
```

If you want to generate the war with java 8 as target just remove the src/test directory and use `pom.jdk8.xml`.
```sh
rm -rf src/test
mvn package -f pom.jdk8.xml [-Dapache-jsp.scope=compile]
```

## Use with reverse-proxy

It is possible to use PlantUML with a reverse proxy.

You can find this and other examples [here](https://github.com/plantuml/plantuml-server/tree/master/examples).
