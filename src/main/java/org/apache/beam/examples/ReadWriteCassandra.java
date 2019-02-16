package org.apache.beam.examples;

import java.util.Arrays;
import java.util.UUID;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.TypeDescriptor;

public class ReadWriteCassandra {
	public static void main(String[] args) {
		Config config = Config.create(false);
		Pipeline p = Pipeline.create(config.pipelineOptions());

		p.apply(CassandraIO.<Table1>read().withHosts(Arrays.asList(config.cassandraHosts()))
				.withPort(config.cassandraPort()).withKeyspace(config.cassandraKeyspace())
				.withEntity(Table1.class).withTable(config.cassandraTable1())
				.withCoder(SerializableCoder.of(Table1.class)).withWhere("data='Whether'"))

				.apply(MapElements.into(TypeDescriptor.of(Table3.class)).via(row1 -> {
					return new Table3(row1.data, UUID.randomUUID(), UUID.randomUUID());
				}))

				.apply(CassandraIO.<Table3>write().withHosts(Arrays.asList(config.cassandraHosts()))
						.withPort(config.cassandraPort()).withKeyspace(config.cassandraKeyspace())
						.withEntity(Table3.class));

		p.run();
	}
}
