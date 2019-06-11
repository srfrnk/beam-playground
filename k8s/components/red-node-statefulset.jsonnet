local imageVersion = std.extVar('IMAGE_VERSION');
{
  apiVersion: 'apps/v1',
  kind: 'StatefulSet',
  metadata: {
    name: 'red-node',
  },
  spec: {
    serviceName: 'red-node',
    replicas: 1,
    selector: {
      matchLabels: {
        app: 'red-node',
      },
    },
    template: {
      metadata: {
        labels: {
          app: 'red-node',
        },
      },
      spec: {
        containers: [
          {
            name: 'red-node',
            image: 'srfrnk/red-node:' + imageVersion,
            ports: [
              {
                containerPort: 1880,
                name: 'ui',
              },
            ],
            resources: {
              limits: {
                cpu: '1000m',
                memory: '1Gi',
              },
              requests: {
                cpu: '100m',
                memory: '0.1Gi',
              },
            },
            volumeMounts: [
              {
                name: 'red-node-data',
                mountPath: '/data',
              },
            ],
          },
        ],
      },
    },
    volumeClaimTemplates: [
      {
        metadata: {
          name: 'red-node-data',
        },
        spec: {
          accessModes: [
            'ReadWriteOnce',
          ],
          storageClassName: 'red-node-storage',
          resources: {
            requests: {
              storage: '1Gi',
            },
          },
        },
      },
    ],
  },
}
