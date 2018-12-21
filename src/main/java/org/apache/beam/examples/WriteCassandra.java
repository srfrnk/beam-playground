package org.apache.beam.examples;

import java.util.Arrays;
import java.util.UUID;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;

public class WriteCassandra {
	public static void main(String[] args) {
		Pipeline p = Pipeline.create(Config.getPipelineOptions());

		PCollection<String> input = p
				.apply(Create.of(Arrays.asList("To be, or not to be: that is the question:",
						"Whether 'tis nobler in the mind to suffer", "The slings and arrows of fortune,",
						"Or to take arms against a sea of troubles,")))
				.setCoder(StringUtf8Coder.of())

				.apply(ParDo.of(new DoFn<String, String>() {
					private static final long serialVersionUID = 1;

					@ProcessElement
					public void processElement(ProcessContext c) {
						String[] words = c.element().split("\\s");
						for (String word : words) {
							c.output(word);
						}
					}
				}));

		input.apply(MapElements.into(TypeDescriptor.of(Table1.class)).via(s -> {
			return new Table1(String.format("%tT %s", System.currentTimeMillis(), s), UUID.randomUUID());
		})).apply(CassandraIO.<Table1>write().withHosts(Arrays.asList(Config.getCassandraHosts()))
				.withPort(Config.getCassandraPort()).withKeyspace(Config.getCassandraKeyspace())
				.withEntity(Table1.class));

		input.apply(MapElements.into(TypeDescriptor.of(Table2.class)).via(s -> {
			return new Table2(String.format("%tT %s", System.currentTimeMillis(), s), UUID.randomUUID());
		})).apply(CassandraIO.<Table2>write().withHosts(Arrays.asList(Config.getCassandraHosts()))
				.withPort(Config.getCassandraPort()).withKeyspace(Config.getCassandraKeyspace())
				.withEntity(Table2.class));

		p.run();
	}
}
