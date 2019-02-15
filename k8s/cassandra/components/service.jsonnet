{
  apiVersion: 'v1',
  kind: 'Service',
  metadata: {
    labels: {
      app: 'cassandra',
    },
    name: 'cassandra',
  },
  spec: {
    ports: [
      {
        protocol: 'TCP',
        port: 9042,
        targetPort: 9042,
      },
    ],
    selector: {
      app: 'cassandra',
    },
    clusterIP: 'None',
  },
}
