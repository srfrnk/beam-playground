{
  apiVersion: 'v1',
  kind: 'Service',
  metadata: {
    name: 'nifi',
  },
  spec: {
    ports: [
      {
        name: 'ui',
        port: 8082,
        targetPort: 8080,
      },
    ],
    selector: {
      app: 'nifi',
    },
  },
}
