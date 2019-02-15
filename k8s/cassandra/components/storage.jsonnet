{
  kind: 'StorageClass',
  apiVersion: 'storage.k8s.io/v1',
  metadata: {
    name: 'cassandra-storage',
  },
  reclaimPolicy: 'Retain',
  provisioner: 'k8s.io/minikube-hostpath',
  parameters: null,
}
