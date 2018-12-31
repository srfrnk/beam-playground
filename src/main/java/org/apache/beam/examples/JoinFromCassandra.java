package org.apache.beam.examples;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.io.TextIO;
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

public class JoinFromCassandra {
	public static void main(String[] args) {
		Pipeline p = Pipeline.create(Config.getPipelineOptions(false));

		PCollection<KV<String, Table1>> table1 = p
				.apply("read table1",
						CassandraIO.<Table1>read().withHosts(Arrays.asList(Config.getCassandraHosts()))
								.withPort(Config.getCassandraPort()).withKeyspace(Config.getCassandraKeyspace())
								.withEntity(Table1.class).withTable(Config.getCassandraTable1())
								.withCoder(SerializableCoder.of(Table1.class)))
				.apply("map table1",
						MapElements
								.into(TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptor.of(Table1.class)))
								.via(row -> KV.of(row.data, row)));

		PCollection<KV<String, Table2>> table2 = p
				.apply("read table2",
						CassandraIO.<Table2>read().withHosts(Arrays.asList(Config.getCassandraHosts()))
								.withPort(Config.getCassandraPort()).withKeyspace(Config.getCassandraKeyspace())
								.withEntity(Table2.class).withTable(Config.getCassandraTable2())
								.withCoder(SerializableCoder.of(Table2.class)))
				.apply("map table2",
						MapElements
								.into(TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptor.of(Table2.class)))
								.via(row -> KV.of(row.data, row)));

		TupleTag<Table1> table1Tag = new TupleTag<>();
		TupleTag<Table2> table2Tag = new TupleTag<>();

		PCollection<String> results = KeyedPCollectionTuple.of(table1Tag, table1).and(table2Tag, table2)
				.apply("join", CoGroupByKey.create())
				.apply("stringify", MapElements.into(TypeDescriptors.strings()).via(group -> {
					CoGbkResult value = group.getValue();
					Iterable<Table1> values1 = value.getAll(table1Tag);
					Iterable<Table2> values2 = value.getAll(table2Tag);

					return String.format("%s -> %s : %s", group.getKey(),
							Joiner.on(",").join(Iterables.transform(values1, row -> row.an_id)),
							Joiner.on(",").join(Iterables.transform(values2, row -> row.an_id)));
				}));

		results.apply(TextIO.write().to("output/join_tables").withoutSharding());

		p.run();
	}
}
