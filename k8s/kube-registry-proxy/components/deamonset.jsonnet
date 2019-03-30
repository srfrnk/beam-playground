local env = std.extVar('__ksonnet/environments');
local params = std.extVar('__ksonnet/params').components.deamonset;
{
  apiVersion: 'extensions/v1beta1',
  kind: 'DaemonSet',
  metadata: {
    name: 'kube-registry-proxy',
    namespace: 'kube-system',
    labels: {
      'k8s-app': 'kube-registry',
      'kubernetes.io/cluster-service': 'true',
      version: 'v0.4',
    },
  },
  spec: {
    template: {
      metadata: {
        labels: {
          'k8s-app': 'kube-registry',
          version: 'v0.4',
        },
      },
      spec: {
        containers: [
          {
            name: 'kube-registry-proxy',
            image: 'gcr.io/google_containers/kube-registry-proxy:0.4',
            resources: {
              limits: {
                cpu: '100m',
                memory: '50Mi',
              },
            },
            env: [
              {
                name: 'REGISTRY_HOST',
                value: 'kube-registry.kube-system.svc.cluster.local',
              },
              {
                name: 'REGISTRY_PORT',
                value: '5000',
              },
            ],
            ports: [
              {
                name: 'registry',
                containerPort: 80,
                hostPort: 5000,
              },
            ],
          },
        ],
      },
    },
  },
}
