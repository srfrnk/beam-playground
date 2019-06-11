{
  apiVersion: 'v1',
  kind: 'Service',
  metadata: {
    name: 'red-node',
  },
  spec: {
    ports: [
      {
        name: 'ui',
        port: 1880,
      },
    ],
    selector: {
      app: 'red-node',
    },
  },
}
