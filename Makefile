MINIKUBE_IP=$(shell minikube ip)

FORCE:

clone-beam-cassandra:
	cd .. && git clone git@github.com:srfrnk/beam.git

clone-cassandra-java-driver:
	cd .. && git clone git@github.com:srfrnk/java-driver.git cassandra-java-driver

start-minikube: FORCE
	minikube start

stop-minikube: FORCE
	minikube stop

proxy: FORCE
	parallel ::: \
		"kubectl proxy" \
		"kubectl port-forward statefulset/zoo 2181:2181" \
		"kubectl port-forward statefulset/cassandra 9042:9042" \
		"kubectl port-forward svc/flink-jobmanager 8081:8081" \
		"kubectl port-forward svc/nifi 8082:8082" \
		"kubectl port-forward svc/red-node 1880:1880" \
		"kubectl port-forward pod/kafka-0 9094:9094 32400:9092" \
		"kubectl port-forward pod/kafka-1 32401:9092" \
		"kubectl port-forward pod/kafka-2 32402:9092"

kill-proxy: FORCE
	- ps aux | grep "kubectl port-forward" | awk '{print $$2}' | xargs kill
	- ps aux | grep "kubectl proxy" | awk '{print $$2}' | xargs kill

watch-pods: FORCE
	watch "kubectl get pods"

start-all: FORCE start-minikube start-kafka start-cassandra start-flink watch-pods

deploy: TIMESTAMP=$(shell date +%y%m%d-%H%M -u)
deploy: FORCE
	eval "$$(minikube docker-env)" && \
	docker build ./k8s/images/flink -t srfrnk/flink:${TIMESTAMP} --build-arg "VERSION=${TIMESTAMP}" && \
	docker build ./k8s/images/cassandra -t srfrnk/cassandra:${TIMESTAMP} --build-arg "VERSION=${TIMESTAMP}" && \
	docker build ./k8s/images/nifi -t srfrnk/nifi:${TIMESTAMP} --build-arg "VERSION=${TIMESTAMP}" && \
	docker build ./k8s/images/red-node -t srfrnk/red-node:${TIMESTAMP} --build-arg "VERSION=${TIMESTAMP}" && \
	ks env --dir ./k8s set minikube --server=https://$(MINIKUBE_IP):8443 && \
	ks apply --dir ./k8s minikube -V IMAGE_VERSION=${TIMESTAMP}

create-schema: FORCE
	cqlsh -e "CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3}; \
		CREATE TABLE IF NOT EXISTS test.table1 ( data text, an_id uuid,another_id uuid, PRIMARY KEY(data,an_id) ); \
		CREATE TABLE IF NOT EXISTS test.table2 ( data text, an_id uuid,another_id uuid, PRIMARY KEY(data,an_id) ); \
		CREATE TABLE IF NOT EXISTS test.table3 ( data text, an_id uuid,another_id uuid, PRIMARY KEY(data,an_id) );"

drop-schema: FORCE
	cqlsh -e "DROP TABLE IF EXISTS test.table1;\
		DROP TABLE IF EXISTS test.table2;\
		DROP KEYSPACE IF EXISTS test;"

truncate-data: FORCE
	cqlsh -e "TRUNCATE test.table1; TRUNCATE test.table2;"

clear:
	@clear

run: clear
	gradle join-from-cassandra-to-cassandra -Drunner=direct

build-cassandra-java-driver:
	mvn -f ../cassandra-java-driver/driver-core package
	cp ../cassandra-java-driver/driver-core/target/*-shaded.jar ../public-jars

	mvn -f ../cassandra-java-driver/driver-mapping package
	cp ../cassandra-java-driver/driver-mapping/target/*-shaded.jar ../public-jars

	mvn -f ../cassandra-java-driver/driver-extras package
	cp ../cassandra-java-driver/driver-extras/target/*-shaded.jar ../public-jars

build-beam:
	gradle -p ../beam/model/pipeline shadowJar
	cp ../beam/model/pipeline/build/libs/*-SNAPSHOT.jar ../public-jars

	gradle -p ../beam/sdks/java/core shadowJar
	cp ../beam/sdks/java/core/build/libs/*-SNAPSHOT.jar ../public-jars

	gradle -p ../beam/sdks/java/io/cassandra build
	cp ../beam/sdks/java/io/cassandra/build/libs/*-SNAPSHOT.jar ../public-jars

	gradle -p ../beam/runners/core-java build
	cp ../beam/runners/core-java/build/libs/*-SNAPSHOT.jar ../public-jars

	gradle -p ../beam/runners/core-construction-java build
	cp ../beam/runners/core-construction-java/build/libs/*-SNAPSHOT.jar ../public-jars

	gradle -p ../beam/runners/flink shadowJar
	cp ../beam/runners/flink/1.7/build/libs/*-SNAPSHOT.jar ../public-jars

	gradle -p ../beam/runners/direct-java shadowJar
	cp ../beam/runners/direct-java/build/libs/*-SNAPSHOT.jar ../public-jars

setup-minikube-docker-registry:
	eval "$(minikube docker-env)"
