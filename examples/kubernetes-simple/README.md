# PlantUML Kubernetes Deployment

In this example, PlantUML is deployed on an Kubernetes cluster using a `Deployment`, a `Service` and an `Ingress`.

## Quick start

Install:

```bash
# Hint: Adjust the Ingress host to your URL

kubectl create ns plantuml
kubectl -n plantuml apply -f deployment.yaml
```

Uninstall:

```bash
kubectl -n plantuml delete -f deployment.yaml
kubectl delete ns plantuml
```

## TLS configuration

Create a TLS `Secret` and extend the `Ingress` spec with a TLS configuration:

```bash
[...]
  tls:
  - hosts:
    - plantuml-example.localhost
    secretName: plantuml-tls
```

Since the `Ingress Controller` terminates the TLS and routes `http` to the application, we might need to tell the application explicitly that it got a forwarded request.

This configuration changes depending on the `Ingress Controller`. Here an nginx example:

```
annotations:
  kubernetes.io/ingress.class: nginx
  nginx.ingress.kubernetes.io/configuration-snippet: |
    more_set_headers "X-Forwarded-Proto: https";
```

## Useful commands

```bash
# see whats going on inside your Deployment
kubectl -n plantuml logs -l "app.kubernetes.io/name=plantuml"
```
