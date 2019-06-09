local imageVersion = std.extVar('IMAGE_VERSION');
{
  apiVersion: 'extensions/v1beta1',
  kind: 'Deployment',
  metadata: {
    name: 'flink-taskmanager',
  },
  spec: {
    replicas: 3,
    template: {
      metadata: {
        labels: {
          app: 'flink',
          component: 'taskmanager',
        },
      },
      spec: {
        containers: [
          {
            name: 'taskmanager',
            image: 'srfrnk/flink:' + imageVersion,
            args: [
              'taskmanager',
            ],
            ports: [
              {
                containerPort: 6121,
                name: 'data',
              },
              {
                containerPort: 6122,
                name: 'rpc',
              },
              {
                containerPort: 6125,
                name: 'query',
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
