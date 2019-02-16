package org.apache.beam.examples;

import java.util.Arrays;
import java.util.UUID;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;

public class JoinFromCassandraToCassandra {
	public static void main(String[] args) {
		Config config = Config.create(false);
		Pipeline p = Pipeline.create(config.pipelineOptions());

		PCollection<KV<String, Table1>> table1 = p.apply("read table1",
				CassandraIO.<Table1>read().withHosts(Arrays.asList(config.cassandraHosts()))
						.withPort(config.cassandraPort()).withKeyspace(config.cassandraKeyspace())
						.withEntity(Table1.class).withTable(config.cassandraTable1())
						.withCoder(SerializableCoder.of(Table1.class)))
				.apply("map table1",
						MapElements
								.into(TypeDescriptors.kvs(TypeDescriptors.strings(),
										TypeDescriptor.of(Table1.class)))
								.via(row -> KV.of(row.data, row)));

		PCollection<KV<String, Table2>> table2 = p.apply("read table2",
				CassandraIO.<Table2>read().withHosts(Arrays.asList(config.cassandraHosts()))
						.withPort(config.cassandraPort()).withKeyspace(config.cassandraKeyspace())
						.withEntity(Table2.class).withTable(config.cassandraTable2())
						.withCoder(SerializableCoder.of(Table2.class)))
				.apply("map table2",
						MapElements
								.into(TypeDescriptors.kvs(TypeDescriptors.strings(),
										TypeDescriptor.of(Table2.class)))
								.via(row -> KV.of(row.data, row)));

		TupleTag<Table1> table1Tag = new TupleTag<>();
		TupleTag<Table2> table2Tag = new TupleTag<>();

		KeyedPCollectionTuple.of(table1Tag, table1).and(table2Tag, table2)
				.apply("join", CoGroupByKey.create())

				.apply("stringify", MapElements.into(TypeDescriptor.of(Table3.class)).via(group -> {
					CoGbkResult value = group.getValue();
					Iterable<Table1> values1 = value.getAll(table1Tag);
					Iterable<Table2> values2 = value.getAll(table2Tag);

					String s = String.format("%s -> %d : %d", group.getKey(),
							Iterables.size(values1),
							Iterables.size(values2));
					return new Table3(s, UUID.randomUUID(), UUID.randomUUID());
				}))

				.apply("write to cassandra table3",
						CassandraIO.<Table3>write()
								.withHosts(Arrays.asList(config.cassandraHosts()))
								.withPort(config.cassandraPort())
								.withKeyspace(config.cassandraKeyspace()).withEntity(Table3.class));

		p.run();
	}
}
