#!/bin/bash
set -e

echo "🚀 Starting Minikube..."
minikube start
kubectl config use-context minikube

echo "✅ Kubernetes cluster ready"

echo
echo "🔨 Building Docker images (backend services)..."

# Build images using Dockerfiles (NOT docker-compose up)
docker build -t organization-service:latest ./organization-service
docker build -t chatbot-service:latest ./chatbot-service
docker build -t api-gateway:latest ./gateway-service

echo "📦 Loading images into Minikube..."
minikube image load organization-service:latest
minikube image load chatbot-service:latest
minikube image load api-gateway:latest

echo
echo "🗄️ Deploying PostgreSQL..."
kubectl apply -f organization-service/k8s/postgres-secret.yaml
kubectl apply -f organization-service/k8s/postgres-configmap.yaml
kubectl apply -f organization-service/k8s/postgres-service.yaml
kubectl apply -f organization-service/k8s/postgres-statefulset.yaml

kubectl rollout status statefulset/postgres

echo
echo "⚙️ Deploying Organization Service..."
kubectl apply -f organization-service/k8s/organization-service.yaml
kubectl apply -f organization-service/k8s/organization-deployment.yaml
kubectl rollout status deployment/organization-service

echo
echo "⚙️ Deploying Chatbot Service..."
kubectl apply -f chatbot-service/k8s/chatbot-service.yaml
kubectl apply -f chatbot-service/k8s/chatbot-deployment.yaml
kubectl rollout status deployment/chatbot-service

echo
echo "🌐 Deploying API Gateway..."
kubectl apply -f gateway-configmap.yaml
kubectl apply -f gateway-secret.yaml
kubectl apply -f gateway-deployment.yaml
kubectl apply -f gateway-service.yaml
kubectl rollout status deployment/api-gateway

echo
echo "✅ All services deployed successfully"
echo "👉 Run: minikube service api-gateway --url"
