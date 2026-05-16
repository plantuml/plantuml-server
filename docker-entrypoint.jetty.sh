#!/bin/sh
# cspell:words mkdir
# cspell:enableCompoundWords
###########################################################

# ensure context path starts with a slash
export CONTEXT_PATH="/${BASE_URL#'/'}"

# Clean up stale Jetty WAR extraction folders from previous container runs.
# See https://github.com/plantuml/plantuml-server/issues/416
rm -rf /tmp/jetty-*-plantuml_war-* 2>/dev/null || true

# base image entrypoint
if [ -x /docker-entrypoint.sh ]; then
    /docker-entrypoint.sh "$@"
else
    exec "$@"
fi
