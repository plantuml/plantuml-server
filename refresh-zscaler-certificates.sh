#!/usr/bin/env bash

echo "Refreshing ZScaler Certificates"
DIR_NAME=$(mktemp --directory -t zscaler-certs-XXXXXXXXXX)
mkdir --parents "${DIR_NAME}"
openssl s_client -showcerts -verify 5 -connect www.google.com:443 -servername www.google.com < /dev/null \
  | awk --assign dir="$DIR_NAME" '/BEGIN/,/END/{ if(/BEGIN/){a++}; out=dir"/zscaler-cert"a".crt"; print >out}'
cp "${DIR_NAME}"/*.crt /usr/local/share/ca-certificates/
update-ca-certificates
rm --recursive --force "${DIR_NAME}"

