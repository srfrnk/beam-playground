local env = std.extVar('__ksonnet/environments');
local params = std.extVar('__ksonnet/params').components.statefulset;
{
  apiVersion: 'apps/v1',
  kind: 'StatefulSet',
  metadata: {
    name: 'kube-registry-v0',
    namespace: 'kube-system',
    labels: {
      'k8s-app': 'kube-registry',
      version: 'v0',
    },
  },
  spec: {
    replicas: 1,
    selector: {
      matchLabels: {
        'k8s-app': 'kube-registry',
        version: 'v0',
      },
    },
    serviceName: 'kube-registry',
    template: {
      metadata: {
        labels: {
          'k8s-app': 'kube-registry',
          version: 'v0',
        },
      },
      spec: {
        containers: [
          {
            name: 'registry',
            image: 'registry:2.5.1',
            resources: {
              limits: {
                cpu: '100m',
                memory: '100Mi',
              },
              requests: {
                cpu: '100m',
                memory: '100Mi',
              },
            },
            env: [
              {
                name: 'REGISTRY_HTTP_SECRET',
                value: 'null_secret',
              },
              {
                name: 'REGISTRY_HTTP_ADDR',
                value: ':5000',
              },
              {
                name: 'REGISTRY_STORAGE_FILESYSTEM_ROOTDIRECTORY',
                value: '/var/lib/registry',
              },
            ],
            volumeMounts: [
              {
                name: 'image-store',
                mountPath: '/var/lib/registry',
              },
            ],
            ports: [
              {
                containerPort: 5000,
                name: 'registry',
                protocol: 'TCP',
              },
            ],
          },
        ],
        volumes: [
          {
            name: 'image-store',
            hostPath: {
              path: '/data/registry/',
            },
          },
        ],
      },
    },
  },
}
