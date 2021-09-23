# PlantUML Server

![workflow status](https://github.com/HeinrichAD/plantuml-server/actions/workflows/main.yml/badge.svg)
[![docker pulls](https://img.shields.io/docker/pulls/heinrichad/plantuml-server.svg)](https://hub.docker.com/r/heinrichad/plantuml-server)

PlantUML Server is a web application to generate UML diagrams on-the-fly.

![PlantUML Server](https://raw.githubusercontent.com/heinrichad/plantuml-server/develop/screenshots/screenshot.png)

To know more about PlantUML, please visit https://plantuml.com.


## About this Fork

### `master` Branch

The `master` branch should be as far as possible always identical with the original repository [plantuml/plantuml-server](https://github.com/plantuml/plantuml-server).
The GitHub App [Pull](https://github.com/apps/pull) is used exactly for this purpose.
It regularly checks for changes in the original repository and transfers them to this `master` branch.
If you want to start the synchronization manually, you can do this via the following URL: https://pull.git.ci/process/HeinrichAD/plantuml-server

The only change from the original state is a slightly adjusted GitHub workflow.

### `develop` Branch

The `develop` branch contains additional features and changes that unfortunately did not make it into the original repository.

If possible, the `develop` branch is also automatically updated by the GitHub App [Pull](https://github.com/apps/pull) via a rebase, so that the base is always the same.
If this is not possible without merge conflicts, an appropriate pull request is created.

#### Additional Features

- update all dependencies
  * jetty 9.4 -> jetty 11
  * tomcat 9.0 -> tomcat 10
  * pom.xml artifacts
  * removed or replaced old/deprecated artifacts
- update and fix all junit test
  * switch from `httpunit` (deprecated) to htmlunit
  * use `java.net.URL` for simple tests
- add automated junit tests with GitHub actions
- run junit tests against multiple server:
  * embedded jetty server
  * jetty docker image
  * tomcat docker image
- update `checkstyle` and fix errors
- fix duplicate classes and resources in classpaths
- remove `http://java.sun.com/jsp/jstl/core` and its dependencies and use scriptlets  
  Normally, this could be interpreted as a step backwards.
  However, since the scriptlets are only used once and thus many old dependencies could be removed, this step is worthwhile.
- improve docker images
  * change `BASE_URL` vom build argument to environment variable
  * use `-slim` images to further reduce the image size
- update links to use `https` if possible
- add [GitHub Pages](https://heinrichad.github.io/plantuml-server/index.html)


## Requirements

- jre/jdk 11 or above
- apache maven 3.0.2 or above

## How to run the server

Just run:

```bash
mvn jetty:run
```

The server is now listing to [http://localhost:8080/plantuml](http://localhost:8080/plantuml).
In this way the server is run on an embedded jetty server.

You can specify the port at which it runs:

```bash
mvn jetty:run -Djetty.port=9999
```

## How to run the server with Docker

You can run Plantuml with jetty or tomcat container
```bash
docker run -d -p 8080:8080 heinrichad/plantuml-server:develop-jetty
docker run -d -p 8080:8080 heinrichad/plantuml-server:develop-tomcat
```

The server is now listing to [http://localhost:8080](http://localhost:8080).

### Read-only container

The jetty container supports read-only files system, you can run the read-only mode with:
```bash
docker run -d -p 8080:8080 --read-only -v /tmp/jetty heinrichad/plantuml-server:develop-jetty
```

This makes the container compatible with more restricted environment such as OpenShift, just make sure you mount a volume (can be ephemeral) on `/tmp/jetty`.

### Change base URL

To run plantuml using different base url, change the `docker-compose.yml` file:
```yaml
environment:
  - BASE_URL=plantuml
```

And run `docker-compose up`. This will start a modified version of the image using
the base url `/plantuml`, e.g. http://localhost:8080/plantuml

## How to set PlantUML options

You can apply some option to your PlantUML server with environment variable.

```bash
mvn jetty:run -DTHE_ENV_VARIABLE=THE_ENV_VALUE
```

If you use docker, you can use the `-e` flag:
```bash
docker run -d -p 8080:8080 -e THE_ENV_VARIABLE=THE_ENV_VALUE heinrichad/plantuml-server:develop-jetty
```

You can set all  the following variables:

* `PLANTUML_LIMIT_SIZE`
    * Limits image width and height
    * Default value: `4096`
* `GRAPHVIZ_DOT`
    * Link to 'dot' executable
    * Default value: `/usr/local/bin/dot` or `/usr/bin/dot`
* `PLANTUML_STATS`
    * Set it to `on` to enable [statistics report](https://plantuml.com/statistics-report)
    * Default value: `off`
* `HTTP_AUTHORIZATION`
    * when calling the `proxy` endpoint, the value of `HTTP_AUTHORIZATION` will be used to set the HTTP Authorization header
    * Default value: `null`
* `ALLOW_PLANTUML_INCLUDE`
    * Enables `!include` processing which can read files from the server into diagrams. Files are read relative to the current working directory.
    * Default value: `false`

## Alternate: How to build your docker image

This method uses maven to run the application. That requires internet connectivity.
So, you can use following command to create a self-contained docker image that will "just-work".

*Note: Generate the WAR (instructions further below) prior to running "docker build"*

```bash
docker image build -t plantuml-server:local .
docker run -d -p 8080:8080 plantuml-server:local
```
The server is now listening to [http://localhost:8080/plantuml](http://localhost:8080/plantuml).

You may specify the port in `-p` Docker command line argument.


## How to generate the war

To build the war, just run:
```bash
mvn package
```
at the root directory of the project to produce plantuml.war in the target/ directory.

NOTE: If you want that the generated war includes the `apache-jsp` artifact run:
```bash
mvn package -Dapache-jsp.scope=compile
```
