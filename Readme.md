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


## Monitoring

### logs on user-service
kubectl logs -f -l app.kubernetes.io/name=user-service


## Clean-up

### Sysem prune
docker system prune -f
docker system prune -a --volumes -f

### Alte docker-image löschen
docker image prune -f

## AWS

### Einstellung
docker compose up -d localstack
curl http://localhost:4566/_localstack/health
pip install awscli-local --break-system-packages

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

### Credentials für localstack
aws configure
AWS Access Key ID: test
AWS Secret Access Key: test
Default region name: eu-central-1
Default output format: json

### S3 Bucket erstellen
aws --endpoint-url=http://localhost:4566 \
    s3 mb s3://user-service-logs --region eu-central-1
aws --endpoint-url=http://localhost:4566 s3 ls

#### Test S3
echo "Hello from user-service" > test.log
aws --endpoint-url=http://localhost:4566 \
    s3 cp test.log s3://user-service-logs/test.log
aws --endpoint-url=http://localhost:4566 \
    s3 ls s3://user-service-logs/

## SQS: RabbitMQ ersetzen
- Queue erstellen
aws --endpoint-url=http://localhost:4566 \
    sqs create-queue --queue-name orders-queue

- Prüfen
aws --endpoint-url=http://localhost:4566 \
    sqs list-queues   

— Message senden (wie order-service)
aws --endpoint-url=http://localhost:4566 \
    sqs send-message \
    --queue-url "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/orders-queue" \
    --message-body '{"orderId": "1", "userId": "54321", "status": "NEW"}'

— Message empfangen (wie user-service)
aws --endpoint-url=http://localhost:4566 \
    sqs receive-message \
    --queue-url "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/orders-queue"

- Message löschen
aws --endpoint-url=http://localhost:4566 \
    sqs delete-message \
    --queue-url "http://sqs.eu-central-1.localhost.localstack.cloud:4566/000000000000/orders-queue" \
    --receipt-handle "Yzg1YjAxZmU..."

##  Secrets Manager: Credentials sicher verwalten
### Secret erstellen
- RabbitMQ Credentials als Secret speichern
aws --endpoint-url=http://localhost:4566 \
    secretsmanager create-secret \
    --name "user-service/rabbitmq" \
    --secret-string '{"host":"rabbitmq","port":"5672","username":"guest","password":"guest"}'

- Kafka Secret
aws --endpoint-url=http://localhost:4566 \
    secretsmanager create-secret \
    --name "user-service/kafka" \
    --secret-string '{"brokers":"kafka:9092"}'

- Prüfen
aws --endpoint-url=http://localhost:4566 \
    secretsmanager list-secrets

— Secret abrufen
aws --endpoint-url=http://localhost:4566 \
    secretsmanager get-secret-value \
    --secret-id "user-service/rabbitmq" \
    --query SecretString \
    --output text    

- Secret updaten
aws --endpoint-url=http://localhost:4566 \
    secretsmanager update-secret \
    --secret-id "user-service/rabbitmq" \
    --secret-string '{"host":"rabbitmq","port":"5672","username":"admin","password":"newpassword123"}'    

## Cloud Information 
- Stack deployment
aws --endpoint-url=http://localhost:4566 \
    cloudformation create-stack \
    --stack-name user-service-stack \
    --template-body file://infrastructure.yml

- Status prüfen
aws --endpoint-url=http://localhost:4566 \
    cloudformation describe-stacks \
    --stack-name user-service-stack \
    --query 'Stacks[0].StackStatus'   

- Stack löschen
aws --endpoint-url=http://localhost:4566 \
    cloudformation delete-stack \
    --stack-name user-service-stack    

- Prüfen    
aws --endpoint-url=http://localhost:4566 s3 ls | grep cf
aws --endpoint-url=http://localhost:4566 sqs list-queues | grep cf