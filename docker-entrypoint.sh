#!/bin/sh
# cspell:words mkdir
# cspell:enableCompoundWords
###########################################################

# use environment variables
if [ "$BASE_URL" != "ROOT" ]; then
    mkdir -p "$(dirname "$WEBAPP_PATH/$BASE_URL")"
    mv "$WEBAPP_PATH/ROOT.war" "$WEBAPP_PATH/$BASE_URL.war"
fi

# base image entrypoint
if [ -x /docker-entrypoint.sh ]; then
    /docker-entrypoint.sh "$@"
else
    exec "$@"
fi
