# Nginx simple reverse proxy example

References:
- [Nginx documentation](https://nginx.org/en/docs/)
- [Nginx beginner's guide](https://nginx.org/en/docs/beginners_guide.html)


## Quick start

Be sure to have [`docker-compose.yml`](./docker-compose.yml) and [`nginx.conf`](./nginx.conf) inside your current working directory.

```bash
# start nginx and plantuml server
docker-compose up -d

# stop nginx and plantuml server
docker-compose down
```

Check with `docker ps` if both container are up and running:

```
$ docker ps
CONTAINER ID   IMAGE                            COMMAND                  CREATED         STATUS         PORTS                               NAMES
217e753a0dcf   plantuml/plantuml-server:jetty   "/entrypoint.sh"         4 seconds ago   Up 3 seconds   8080/tcp                            plantuml-server
9b1290c100f5   nginx:alpine                     "/docker-entrypoint.…"   4 seconds ago   Up 3 seconds   0.0.0.0:80->80/tcp, :::80->80/tcp   nginx
```

Open [http://localhost](http://localhost) inside your browser.
YEAH! You are now using PlantUML behind a simple Nginx reverse proxy.


## Nginx configuration

```nginx
...

# PlantUML
location / {
    proxy_set_header  HOST               $host;
    proxy_set_header  X-Forwarded-Host   $host;
    proxy_set_header  X-Forwarded-Proto  $scheme;

    proxy_pass http://plantuml-server:8080/;
}

...
```

- `location /` to reverse complete server
- `proxy_set_header HOST $host` and `proxy_set_header X-Forwarded-Host $host` to replaces local plantuml server ip with FQDN
- `proxy_set_header X-Forwarded-Proto $scheme` to use reverse proxy protocol schema instead of communication schema between reverse proxy and plantuml server
- `proxy_pass http://plantuml-server:8080/` to set reverse proxy path to plantuml server.
  Use the docker container name `plantuml-server` instead of ip addresses.


## Nginx and PlantUML server

```yaml
version: "3"

services:
  plantuml-server:
    image: plantuml/plantuml-server:jetty
    container_name: plantuml-server
    environment:
      - TZ="Europe/Berlin"

  nginx:
    image: nginx:alpine
    container_name: nginx
    ports:
      - "80:80"
    environment:
      - TZ="Europe/Berlin"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
```

- Set `container_name` to use them instead of e.g. ip addresses
- Set the environment `TZ` the ensure the same timezone.
  For example to server timezone (`cat /etc/timezone`)?
- plantuml-server
  * plantuml-server already exposes port `8080` to it's own local network (but not outside).
    Since plantuml-server and nginx are sharing a network, nginx can reach plantuml-server without further settings.
- nginx
  * open/link port `80` to the outside
  * `./nginx.conf:/etc/nginx/nginx.conf:ro` to use your own Nginx configuration (readonly)


## Useful commands

```bash
# see whats going on inside your docker containers
docker logs --tail 50 --follow --timestamps nginx
docker logs --tail 50 --follow --timestamps plantuml-server
```
