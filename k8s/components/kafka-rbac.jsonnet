local k = import 'k.libsonnet';
k.core.v1.list.new([
  {
    kind: 'ClusterRole',
    apiVersion: 'rbac.authorization.k8s.io/v1',
    metadata: {
      name: 'node-reader',
      labels: {
        origin: 'github.com_Yolean_kubernetes-kafka',
      },
    },
    rules: [
      {
        apiGroups: [
          '',
        ],
        resources: [
          'nodes',
        ],
        verbs: [
          'get',
        ],
      },
    ],
  },
  {
    kind: 'ClusterRoleBinding',
    apiVersion: 'rbac.authorization.k8s.io/v1',
    metadata: {
      name: 'kafka-node-reader',
      labels: {
        origin: 'github.com_Yolean_kubernetes-kafka',
      },
    },
    roleRef: {
      apiGroup: 'rbac.authorization.k8s.io',
      kind: 'ClusterRole',
      name: 'node-reader',
    },
    subjects: [
      {
        kind: 'ServiceAccount',
        name: 'default',
        namespace: 'default',
      },
    ],
  },
  {
    kind: 'Role',
    apiVersion: 'rbac.authorization.k8s.io/v1',
    metadata: {
      name: 'pod-labler',
      labels: {
        origin: 'github.com_Yolean_kubernetes-kafka',
      },
    },
    rules: [
      {
        apiGroups: [
          '',
        ],
        resources: [
          'pods',
        ],
        verbs: [
          'get',
          'update',
          'patch',
        ],
      },
    ],
  },
  {
    kind: 'RoleBinding',
    apiVersion: 'rbac.authorization.k8s.io/v1',
    metadata: {
      name: 'kafka-pod-labler',
      labels: {
        origin: 'github.com_Yolean_kubernetes-kafka',
      },
    },
    roleRef: {
      apiGroup: 'rbac.authorization.k8s.io',
      kind: 'Role',
      name: 'pod-labler',
    },
    subjects: [
      {
        kind: 'ServiceAccount',
        name: 'default',
        namespace: 'default',
      },
    ],
  },
])
