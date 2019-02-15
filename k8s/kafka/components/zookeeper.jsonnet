local k = import 'k.libsonnet';
k.core.v1.list.new([
  {
    kind: 'ConfigMap',
    metadata: {
      name: 'zookeeper-config',
    },
    apiVersion: 'v1',
    data: {
      'init.sh': '#!/bin/bash\nset -x\n\n[ -z "$ID_OFFSET" ] && ID_OFFSET=1\nexport ZOOKEEPER_SERVER_ID=$((${HOSTNAME##*-} + $ID_OFFSET))\necho "${ZOOKEEPER_SERVER_ID:-1}" | tee /var/lib/zookeeper/data/myid\ncp -Lur /etc/kafka-configmap/* /etc/kafka/\nsed -i "s/server\\.$ZOOKEEPER_SERVER_ID\\=[a-z0-9.-]*/server.$ZOOKEEPER_SERVER_ID=0.0.0.0/" /etc/kafka/zookeeper.properties',
      'zookeeper.properties': 'tickTime=2000\ndataDir=/var/lib/zookeeper/data\ndataLogDir=/var/lib/zookeeper/log\nclientPort=2181\ninitLimit=5\nsyncLimit=2\nserver.1=pzoo-0.pzoo:2888:3888:participant\nserver.2=pzoo-1.pzoo:2888:3888:participant\nserver.3=pzoo-2.pzoo:2888:3888:participant\nserver.4=zoo-0.zoo:2888:3888:participant\nserver.5=zoo-1.zoo:2888:3888:participant',
      'log4j.properties': 'log4j.rootLogger=INFO, stdout\nlog4j.appender.stdout=org.apache.log4j.ConsoleAppender\nlog4j.appender.stdout.layout=org.apache.log4j.PatternLayout\nlog4j.appender.stdout.layout.ConversionPattern=[%d] %p %m (%c)%n\n\n# Suppress connection log messages, three lines per livenessProbe execution\nlog4j.logger.org.apache.zookeeper.server.NIOServerCnxnFactory=WARN\nlog4j.logger.org.apache.zookeeper.server.NIOServerCnxn=WARN',
    },
  },
  {
    apiVersion: 'v1',
    kind: 'Service',
    metadata: {
      name: 'pzoo',
    },
    spec: {
      ports: [
        {
          port: 2888,
          name: 'peer',
        },
        {
          port: 3888,
          name: 'leader-election',
        },
      ],
      clusterIP: 'None',
      selector: {
        app: 'zookeeper',
        storage: 'persistent',
      },
    },
  },
  {
    apiVersion: 'v1',
    kind: 'Service',
    metadata: {
      name: 'zoo',
    },
    spec: {
      ports: [
        {
          port: 2888,
          name: 'peer',
        },
        {
          port: 3888,
          name: 'leader-election',
        },
      ],
      clusterIP: 'None',
      selector: {
        app: 'zookeeper',
        storage: 'ephemeral',
      },
    },
  },
  {
    apiVersion: 'v1',
    kind: 'Service',
    metadata: {
      name: 'zookeeper',
    },
    spec: {
      ports: [
        {
          port: 2181,
          name: 'client',
        },
      ],
      selector: {
        app: 'zookeeper',
      },
    },
  },
  {
    apiVersion: 'apps/v1',
    kind: 'StatefulSet',
    metadata: {
      name: 'pzoo',
    },
    spec: {
      selector: {
        matchLabels: {
          app: 'zookeeper',
          storage: 'persistent',
        },
      },
      serviceName: 'pzoo',
      replicas: 3,
      updateStrategy: {
        type: 'OnDelete',
      },
      template: {
        metadata: {
          labels: {
            app: 'zookeeper',
            storage: 'persistent',
          },
          annotations: null,
        },
        spec: {
          terminationGracePeriodSeconds: 10,
          initContainers: [
            {
              name: 'init-config',
              image: 'solsson/kafka-initutils@sha256:18bf01c2c756b550103a99b3c14f741acccea106072cd37155c6d24be4edd6e2',
              command: [
                '/bin/bash',
                '/etc/kafka-configmap/init.sh',
              ],
              volumeMounts: [
                {
                  name: 'configmap',
                  mountPath: '/etc/kafka-configmap',
                },
                {
                  name: 'config',
                  mountPath: '/etc/kafka',
                },
                {
                  name: 'data',
                  mountPath: '/var/lib/zookeeper/data',
                },
              ],
            },
          ],
          containers: [
            {
              name: 'zookeeper',
              image: 'solsson/kafka:1.0.2@sha256:7fdb326994bcde133c777d888d06863b7c1a0e80f043582816715d76643ab789',
              env: [
                {
                  name: 'KAFKA_LOG4J_OPTS',
                  value: '-Dlog4j.configuration=file:/etc/kafka/log4j.properties',
                },
              ],
              command: [
                './bin/zookeeper-server-start.sh',
                '/etc/kafka/zookeeper.properties',
              ],
              ports: [
                {
                  containerPort: 2181,
                  name: 'client',
                },
                {
                  containerPort: 2888,
                  name: 'peer',
                },
                {
                  containerPort: 3888,
                  name: 'leader-election',
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
              readinessProbe: {
                exec: {
                  command: [
                    '/bin/sh',
                    '-c',
                    '[ "imok" = "$(echo ruok | nc -w 1 -q 1 127.0.0.1 2181)" ]',
                  ],
                },
              },
              volumeMounts: [
                {
                  name: 'config',
                  mountPath: '/etc/kafka',
                },
                {
                  name: 'data',
                  mountPath: '/var/lib/zookeeper/data',
                },
              ],
            },
          ],
          volumes: [
            {
              name: 'configmap',
              configMap: {
                name: 'zookeeper-config',
              },
            },
            {
              name: 'config',
              emptyDir: {},
            },
          ],
        },
      },
      volumeClaimTemplates: [
        {
          metadata: {
            name: 'data',
          },
          spec: {
            accessModes: [
              'ReadWriteOnce',
            ],
            storageClassName: 'kafka-zookeeper',
            resources: {
              requests: {
                storage: '1Gi',
              },
            },
          },
        },
      ],
    },
  },
  {
    apiVersion: 'apps/v1',
    kind: 'StatefulSet',
    metadata: {
      name: 'zoo',
    },
    spec: {
      selector: {
        matchLabels: {
          app: 'zookeeper',
          storage: 'ephemeral',
        },
      },
      serviceName: 'zoo',
      replicas: 2,
      updateStrategy: {
        type: 'OnDelete',
      },
      template: {
        metadata: {
          labels: {
            app: 'zookeeper',
            storage: 'ephemeral',
          },
          annotations: null,
        },
        spec: {
          terminationGracePeriodSeconds: 10,
          initContainers: [
            {
              name: 'init-config',
              image: 'solsson/kafka-initutils@sha256:18bf01c2c756b550103a99b3c14f741acccea106072cd37155c6d24be4edd6e2',
              command: [
                '/bin/bash',
                '/etc/kafka-configmap/init.sh',
              ],
              env: [
                {
                  name: 'ID_OFFSET',
                  value: '4',
                },
              ],
              volumeMounts: [
                {
                  name: 'configmap',
                  mountPath: '/etc/kafka-configmap',
                },
                {
                  name: 'config',
                  mountPath: '/etc/kafka',
                },
                {
                  name: 'data',
                  mountPath: '/var/lib/zookeeper/data',
                },
              ],
            },
          ],
          containers: [
            {
              name: 'zookeeper',
              image: 'solsson/kafka:1.0.2@sha256:7fdb326994bcde133c777d888d06863b7c1a0e80f043582816715d76643ab789',
              env: [
                {
                  name: 'KAFKA_LOG4J_OPTS',
                  value: '-Dlog4j.configuration=file:/etc/kafka/log4j.properties',
                },
              ],
              command: [
                './bin/zookeeper-server-start.sh',
                '/etc/kafka/zookeeper.properties',
              ],
              ports: [
                {
                  containerPort: 2181,
                  name: 'client',
                },
                {
                  containerPort: 2888,
                  name: 'peer',
                },
                {
                  containerPort: 3888,
                  name: 'leader-election',
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
              readinessProbe: {
                exec: {
                  command: [
                    '/bin/sh',
                    '-c',
                    '[ "imok" = "$(echo ruok | nc -w 1 -q 1 127.0.0.1 2181)" ]',
                  ],
                },
              },
              volumeMounts: [
                {
                  name: 'config',
                  mountPath: '/etc/kafka',
                },
                {
                  name: 'data',
                  mountPath: '/var/lib/zookeeper/data',
                },
              ],
            },
          ],
          volumes: [
            {
              name: 'configmap',
              configMap: {
                name: 'zookeeper-config',
              },
            },
            {
              name: 'config',
              emptyDir: {},
            },
            {
              name: 'data',
              emptyDir: {},
            },
          ],
        },
      },
    },
  },
])
