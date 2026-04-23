#!/usr/bin/env bash

set -euo pipefail

echo "🚀 Starting Minikube..."
minikube start

echo "✅ Switching kubectl context to minikube..."
kubectl config use-context minikube

echo "🔍 Verifying Kubernetes cluster access..."
kubectl cluster-info
kubectl get nodes

echo "────────────────────────────────────────────"
echo "🗄️  Deploying PostgreSQL (organization-service)"
echo "────────────────────────────────────────────"

# PostgreSQL prerequisites
kubectl apply -f organization-service/k8s/postgres-secret.yaml
kubectl apply -f organization-service/k8s/postgres-configmap.yaml

# PostgreSQL service + statefulset
kubectl apply -f organization-service/k8s/postgres-service.yaml
kubectl apply -f organization-service/k8s/postgres-statefulset.yaml

echo "⏳ Waiting for PostgreSQL StatefulSet to be ready..."
kubectl rollout status statefulset/postgres --timeout=180s

echo "────────────────────────────────────────────"
echo "⚙️  Deploying backend services"
echo "────────────────────────────────────────────"

# Organization service
kubectl apply -f organization-service/k8s/organization-service.yaml
kubectl apply -f organization-service/k8s/organization-deployment.yaml

echo "⏳ Waiting for organization-service deployment..."
kubectl rollout status deployment/organization-service --timeout=180s

# Chatbot service
kubectl apply -f chatbot-service/k8s/chatbot-service.yaml
kubectl apply -f chatbot-service/k8s/chatbot-deployment.yaml

echo "⏳ Waiting for chatbot-service deployment..."
kubectl rollout status deployment/chatbot-service --timeout=180s

echo "────────────────────────────────────────────"
echo "🌐 Deploying Gateway"
echo "────────────────────────────────────────────"

kubectl apply -f gateway-configmap.yaml
kubectl apply -f gateway-secret.yaml
kubectl apply -f gateway-deployment.yaml
kubectl apply -f gateway-service.yaml

echo "⏳ Waiting for gateway deployment..."
kubectl rollout status deployment/gateway --timeout=180s

echo "────────────────────────────────────────────"
echo "✅ Deployment complete!"
echo "────────────────────────────────────────────"

echo ""
echo "🔎 Useful commands:"
echo "kubectl get pods"
echo "kubectl get svc"
echo "kubectl logs -f deployment/gateway"
echo "minikube service list"