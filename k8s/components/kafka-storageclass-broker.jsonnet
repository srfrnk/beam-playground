local k = import 'k.libsonnet';
{
  kind: 'StorageClass',
  apiVersion: 'storage.k8s.io/v1',
  metadata: {
    name: 'kafka-broker',
  },
  provisioner: 'k8s.io/minikube-hostpath',
  reclaimPolicy: 'Retain',
  parameters: null,
}
