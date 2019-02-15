# BEAM Playground

## Install Tool-Chain

### Minikube
[See here...](https://kubernetes.io/docs/tasks/tools/install-minikube/)

### Ksonnet
[See here...](https://ksonnet.io/get-started/#quick-start)

### cqlsh (cmd)
- [See here...](http://cassandra.apache.org/doc/latest/getting_started/installing.html)
- Make sure you disable cassandra from running on startup. (i.e. in linux: `sudo update-rc.d cassandra disable`)

### flink (cmd)
- [See here...](https://ci.apache.org/projects/flink/flink-docs-release-1.7/tutorials/local_setup.html#setup-download-and-start-flink)
- You don't need to have the server running. Stop it.
- Make sure your `PATH` points to the Flink `bin` directory. (i.e. in linux: `echo 'export PATH=$PATH:/opt/flink/bin' >> ~/.bashrc`)

### Java 8
[See here...](https://openjdk.java.net/install/)

### Gradle
[See here...](https://gradle.org/install/)

### GNU Parallel
[See here...](https://www.gnu.org/software/parallel/)

## Prepare the environment

### Start minikube
make start-minikube

### Start cassandra server
```bash
make start-cassandra
```

### Create the schema
```bash
make create-schema
```

### Cleanup
When done you can use these to cleanup
```bash
make drop-schema
make stop-cassandra
```

## Run the test
### Write to Cassandra
```bash
gradle write-cassandra
```
### Read from Cassandra
```bash
gradle read-cassandra
```
