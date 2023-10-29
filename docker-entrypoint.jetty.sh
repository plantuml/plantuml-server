#!/bin/sh
# cspell:words mkdir
# cspell:enableCompoundWords
###########################################################

# ensure context path starts with a slash
export CONTEXT_PATH="/${BASE_URL#'/'}"

# base image entrypoint
if [ -x /docker-entrypoint.sh ]; then
    /docker-entrypoint.sh "$@"
else
    exec "$@"
fi
