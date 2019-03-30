local env = std.extVar('__ksonnet/environments');
local params = std.extVar('__ksonnet/params').components.service;
{
  apiVersion: 'v1',
  kind: 'Service',
  metadata: {
    name: 'kube-registry',
    namespace: 'kube-system',
    labels: {
      'k8s-app': 'kube-registry',
    },
  },
  spec: {
    selector: {
      'k8s-app': 'kube-registry',
    },
    ports: [
      {
        name: 'registry',
        port: 5000,
        protocol: 'TCP',
      },
    ],
  },
}
