FORCE:

clone-beam-cassandra:
	cd .. && git clone git@github.com:srfrnk/beam.git

clone-cassandra-java-driver:
	cd .. && git clone git@github.com:srfrnk/java-driver.git cassandra-java-driver

start-cassandra: FORCE
	docker run -d --rm --name cassandra --network host cassandra

stop-cassandra: FORCE
	docker kill cassandra

create-schema: FORCE
	cqlsh -e "CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1}; \
		CREATE TABLE IF NOT EXISTS test.table1 ( data text, an_id uuid, PRIMARY KEY(data) ); \
		CREATE TABLE IF NOT EXISTS test.table2 ( data text, an_id uuid, PRIMARY KEY(data) );"

drop-schema: FORCE
	cqlsh -e "DROP TABLE IF EXISTS test.table1;\
		DROP TABLE IF EXISTS test.table2;\
		DROP KEYSPACE IF EXISTS test;"

truncate-data: FORCE
	cqlsh -e "TRUNCATE test.table1; TRUNCATE test.table2;"

clear:
	@clear

run: clear
	gradle join-from-cassandra -Drunner=flink-cluster

tt:
	flink run -d -c org.apache.beam.examples.ReadWriteCassandra build/libs/beam-playground-0.1-all.jar
	kubectl cp ./src mgmt-0:/beam-playground/src && kubectl cp ./build.gradle mgmt-0:/beam-playground && kubectl exec -it mgmt-0 -- bash -c gradle -p /beam-playground read-write-cassandra -Drunner=flink-cluster

build-cassandra-java-driver:
	mvn -f ../cassandra-java-driver package

build-beam-cassandra:
	gradle -p ../beam/sdks/java/io/cassandra shadowJar
