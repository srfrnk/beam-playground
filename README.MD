# BEAM Playground

## Prepare the environment
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
