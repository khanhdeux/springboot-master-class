#!/bin/bash
set -e

# HIER DIE NEUE VERSION DEFINIEREN
VERSION="v4"

echo "1. Baue Java App für Version $VERSION..."
cd user-service
# Stelle sicher, dass die Änderung im UserController auf "v3" steht!
mvn clean package -DskipTests 
# Wir taggen das Image explizit mit der Versionsnummer
docker build -t user-service:$VERSION .
cd ..

echo "2. Lade Image $VERSION in KIND..."
kind load docker-image user-service:$VERSION

echo "3. Produktion-Style Update mit Helm..."
# Wir überschreiben den Tag in den Helm-Values direkt beim Befehl
helm upgrade user-service ./charts/user-service \
  --set image.tag=$VERSION \
  --set image.pullPolicy=IfNotPresent

echo "4. Warte auf Zero-Downtime Rollout..."
# Dieser Befehl wartet, bis die neuen Pods "Ready" sind
kubectl rollout status deployment/user-service