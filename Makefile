FORCE:

clone-beam-cassandra:
	cd .. && git clone git@github.com:srfrnk/beam.git

clone-cassandra-java-driver:
	cd .. && git clone git@github.com:srfrnk/java-driver.git cassandra-java-driver

start-minikube: FORCE
	minikube start

stop-minikube: FORCE
	minikube stop

start-cassandra: FORCE
	ks apply --dir ./k8s/cassandra minikube

stop-cassandra: FORCE
	ks delete --dir ./k8s/cassandra minikube

start-flink: FORCE
	ks apply --dir ./k8s/flink minikube

stop-flink: FORCE
	ks delete --dir ./k8s/flink minikube

start-kafka: FORCE
	ks apply --dir ./k8s/kafka minikube

stop-kafka: FORCE
	ks delete --dir ./k8s/kafka minikube

start-proxy: FORCE
	parallel ::: \
		"kubectl proxy" \
		"kubectl port-forward statefulset/zoo 2181:2181" \
		"kubectl port-forward statefulset/cassandra 9042:9042" \
		"kubectl port-forward svc/flink-jobmanager 8081:8081" \
		"kubectl port-forward pod/kafka-0 9094:9094 32400:9092" \
		"kubectl port-forward pod/kafka-1 32401:9092" \
		"kubectl port-forward pod/kafka-2 32402:9092"

create-flink-image: FORCE
	docker build ./k8s/flink/image -t srfrnk/flink:latest
	docker push srfrnk/flink:latest

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
	mvn -f ../cassandra-java-driver/driver-core package
	cp ../cassandra-java-driver/driver-core/target/*-shaded.jar ../public-jars

	mvn -f ../cassandra-java-driver/driver-mapping package
	cp ../cassandra-java-driver/driver-mapping/target/*-shaded.jar ../public-jars

	mvn -f ../cassandra-java-driver/driver-extras package
	cp ../cassandra-java-driver/driver-extras/target/*-shaded.jar ../public-jars

build-beam-cassandra:
	gradle -p ../beam/sdks/java/io/cassandra shadowJar
	cp ../beam/sdks/java/io/cassandra/build/libs/*.jar ../public-jars
