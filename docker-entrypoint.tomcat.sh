#!/bin/sh
# cspell:words mkdir
# cspell:enableCompoundWords
###########################################################

# choose war file name so that context path is correctly set based on BASE_URL,
# following the rules from https://tomcat.apache.org/tomcat-9.0-doc/config/context.html#Naming,
# specifically remove leading and trailing slashes and replace the remaining ones by hashes.
export FILE_NAME="$(echo "$BASE_URL" | sed -e 's:^/::' -e 's:/$::' -e 's:/:#:g')"
export FILE_PATH="$WEBAPP_PATH/$FILE_NAME.war"
mv /plantuml.war "$FILE_PATH"

# base image entrypoint
if [ -x /docker-entrypoint.sh ]; then
    /docker-entrypoint.sh "$@"
else
    exec "$@"
fi
