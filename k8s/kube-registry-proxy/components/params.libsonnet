{
  global: {
    // User-defined global parameters; accessible to all component and environments, Ex:
    // replicas: 4,
  },
  components: {
    // Component-level parameters, defined initially from 'ks prototype use ...'
    // Each object below should correspond to a component in the components/ directory
    deamonset: {
      containerPort: 80,
      image: "s",
      name: "deamonset",
      replicas: 1,
    },
    statefulset: {
      containerPort: 80,
      image: "s",
      name: "statefulset",
      replicas: 1,
    },
    service: {
      containerPort: 80,
      image: "s",
      name: "service",
      replicas: 1,
    },
  },
}
