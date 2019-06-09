local imageVersion = std.extVar('IMAGE_VERSION');
{
  apiVersion: 'apps/v1',
  kind: 'StatefulSet',
  metadata: {
    name: 'nifi',
  },
  spec: {
    serviceName: 'nifi',
    replicas: 1,
    selector: {
      matchLabels: {
        app: 'nifi',
      },
    },
    template: {
      metadata: {
        labels: {
          app: 'nifi',
        },
      },
      spec: {
        containers: [
          {
            name: 'nifi',
            image: 'srfrnk/nifi:' + imageVersion,
            ports: [
              {
                containerPort: 8080,
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
          },
        ],
      },
    },
  },
}
