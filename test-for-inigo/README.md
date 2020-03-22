# Test for inigo

## minikube

```bash
minikube start
```

## zookeeper

```bash
git clone git@github.com:DAVFoundation/missioncontrol.git
cd missioncontrol
make build
make deploy-zookeeper
```

## dnsutils

```bash
kubectl apply -f dnsutils.yaml
k exec -it dnsutils sh
dig zookeeper-1.zookeeper.davnn-zookeeper.svc.cluster.local
```
