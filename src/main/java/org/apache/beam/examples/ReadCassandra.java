package org.apache.beam.examples;

import java.util.Arrays;

import com.datastax.driver.core.querybuilder.QueryBuilder;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.TypeDescriptors;

public class ReadCassandra {
	public static void main(String[] args) {
		Pipeline p = Pipeline.create(Config.getPipelineOptions());
		p.apply(CassandraIO.<Table1>read().withHosts(Arrays.asList(Config.getCassandraHosts()))
				.withPort(Config.getCassandraPort()).withKeyspace(Config.getCassandraKeyspace())
				.withEntity(Table1.class).withTable(Config.getCassandraTable1())
				.withCoder(SerializableCoder.of(Table1.class))
				.withWhere(QueryBuilder.eq("data","Whether")))

				.apply(MapElements.into(TypeDescriptors.strings()).via(s -> {
					return String.format("%tT %s %s", System.currentTimeMillis(), s.data, s.an_id);
				}))

				.apply(TextIO.write().to("output/from_table1").withoutSharding());

		p.run();
	}
}
