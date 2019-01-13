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

run:
	@gradle --console=plain join-from-cassandra -Drunner=direct > output/build.log 2>&1

tt:
	flink run -d -c org.apache.beam.examples.WriteCassandra /tmp/beam-playground-0.1-all.jar

.ONESHELL:

build-cassandra-java-driver:
	cd ../cassandra-java-driver
	mvn clean package

build-beam-cassandra:
	cd ../beam/sdks/java/io/cassandra
	gradle clean shadowJar
