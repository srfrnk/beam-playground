local imageVersion = std.extVar('IMAGE_VERSION');
{
  apiVersion: 'apps/v1',
  kind: 'StatefulSet',
  metadata: {
    name: 'flink-jobmanager',
  },
  spec: {
    serviceName: 'flink-jobmanager',
    replicas: 1,
    selector: {
      matchLabels: {
        app: 'flink',
        component: 'jobmanager',
      },
    },
    template: {
      metadata: {
        labels: {
          app: 'flink',
          component: 'jobmanager',
        },
      },
      spec: {
        containers: [
          {
            name: 'jobmanager',
            image: 'srfrnk/flink:' + imageVersion,
            args: [
              'jobmanager',
            ],
            ports: [
              {
                containerPort: 6123,
                name: 'rpc',
              },
              {
                containerPort: 6124,
                name: 'blob',
              },
              {
                containerPort: 6125,
                name: 'query',
              },
              {
                containerPort: 8081,
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
            env: [
              {
                name: 'JOB_MANAGER_RPC_ADDRESS',
                value: 'flink-jobmanager',
              },
            ],
          },
        ],
      },
    },
  },
}
