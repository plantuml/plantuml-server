PlantUML Server 
===============
[![Build Status](https://travis-ci.org/plantuml/plantuml-server.png?branch=master)](https://travis-ci.org/plantuml/plantuml-server)

PlantUML Server is a web application to generate UML diagrams on-the-fly.
 
To know more about PlantUML, please visit http://plantuml.sourceforge.net/.

How to build the project
========================

To build the project you need to install the following components:

 * java jdk 1.6.0 or above
 * apache maven 3.0.2 or above

To build the war, just run "mvn package" at the root directory of the project to produce 
plantuml.war in the target/ directory.

How to testrun the project
==========================

To run the application deployed on an embedded jetty server run "mvn jetty:run" 
and go to http://localhost:8080/plantuml with your favorite web browser (after it finishes
to start up).

Install on OpenShift
====================

The PlantUML Server can be installed on OpenShift directly from the GitHub project using
either the OpenShift web console or the [OpenShift client tools](https://developers.openshift.com/en/getting-started-client-tools.html).
It works on the following cartridges:
  * Tomcat 7 (JBoss EWS 2.0)
  * JBoss Enterprise Application Platform 6 
  * WildFly Application Server 8

Example using the OpenShift client tools:

    rhc -aplantuml app create jbossews-2 http://cartreflect-claytondev.rhcloud.com/github/puzzle/openshift-graphviz-cartridge --from-code=https://github.com/plantuml/plantuml-server.git --no-git

The ``-a`` option defines the name of your PlantUML Server installation, ``plantuml`` in this instance. You are free to specify a different name.
The ``--no-git`` option skips the creation of a local git repository. This also installs Graphviz which is needed for some diagram types, e.g. class diagrams.
