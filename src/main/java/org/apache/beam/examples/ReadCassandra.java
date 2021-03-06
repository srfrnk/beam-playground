package org.apache.beam.examples;

import java.util.Arrays;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.TypeDescriptors;

public class ReadCassandra {
	public static void main(String[] args) {
		Config config = Config.create(false);
		Pipeline p = Pipeline.create(config.pipelineOptions());

		p.apply(CassandraIO.<Table1>read().withHosts(Arrays.asList(config.cassandraHosts()))
				.withPort(config.cassandraPort()).withKeyspace(config.cassandraKeyspace())
				.withTable(config.cassandraTable1()).withEntity(Table1.class)
				.withCoder(SerializableCoder.of(Table1.class))
				.withQuery(String.format("select * from %s.%s where data='Whether'",
						config.cassandraKeyspace(), config.cassandraTable1())))
				.apply(MapElements.into(TypeDescriptors.strings()).via(s -> {
					return String.format("%tT %s %s", System.currentTimeMillis(), s.data, s.an_id);
				}))

				.apply(TextIO.write().to("output/from_table1").withoutSharding());

		p.run();
	}
}
