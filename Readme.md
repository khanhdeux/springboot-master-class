## Create jaeger tracing server
docker run -d --name jaeger \
  -e COLLECTOR_OTLP_ENABLED=true \
  -p 16686:16686 \
  -p 4318:4318 \
  jaegertracing/all-in-one:latest


## Docker
docker-compose up --build

## Create k8s infratrukture

### Cluster erstellen
kind create cluster --config kind-config.yaml

### Grundstruktur für user-service generieren
mkdir charts
cd charts
helm create user-service

### App bauen - java img erstellen
cd user-service && docker build -t user-service:latest .

### Image in Cluster schieben
kind load docker-image user-service:latest

### Empfangschef - Ingress-Controller
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

- Ingress prüfung 
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=90s

### App-Vorbereitung
- values.yaml
  replicaCount: 2
  service.port: 8080
  ingress.enabled: true
  resources.requests.memory: "512Mi"

### Installation mit Helm
helm install user-service ./charts/user-service

### Service Aktualisierung
helm upgrade user-service ./charts/user-service

### Port-forward im Browser
kubectl port-forward svc/user-service 8080:8080