#!/bin/bash -

SERVICE=plantuml

WEBSERVER=jetty
if [[ $0 == *tomcat* ]] ; then
    WEBSERVER=tomcat
fi

function stop-service {
    echo Stopping $SERVICE ...
    sudo docker stop $SERVICE 2>&1
    sudo docker container rm -f $SERVICE 2>&1
    echo $SERVICE stopped.
}

function start-service {
    echo Starting $SERVICE ...
    sudo docker run \
	--name $SERVICE \
	-d -p 8080:8080 \
	-v /var/log/$SERVICE:/var/log/$WEBSERVER \
	$SERVICE/$SERVICE-server:$WEBSERVER
    echo $SERVICE started.
}

if [[ $1 == --stop ]] ; then
    stop-service
else
    stop-service
    start-service
fi
