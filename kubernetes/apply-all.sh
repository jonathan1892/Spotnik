kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml
kubectl apply -f backend-deployment.yaml
kubectl apply -f backend-service.yaml
kubectl apply -f frontend-deployment.yaml
kubectl apply -f frontend-service.yaml
#kubectl apply -f ingress.yaml