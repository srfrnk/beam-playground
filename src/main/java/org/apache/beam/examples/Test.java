package org.apache.beam.examples;

import java.util.Arrays;
import java.util.UUID;

import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.cassandra.CassandraIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;

public class Test {
	public static void main(String[] args) {
		readCassandra();
		// writeCassandra();
	}

	public static void readCassandra() {
		FlinkPipelineOptions options = PipelineOptionsFactory.create().as(FlinkPipelineOptions.class);
		options.setRunner(FlinkRunner.class);
		options.setFilesToStage(Arrays.asList("build/libs/test-0.1.jar"));
		options.setFlinkMaster("[local]");
		options.setStreaming(false);
		Pipeline p = Pipeline.create(options);

		p.apply(CassandraIO.<Riders>read().withHosts(Arrays.asList("localhost")).withPort(9042).withKeyspace("test")
				.withEntity(Riders.class).withTable("test").withCoder(SerializableCoder.of(Riders.class)))

				.apply(MapElements.into(TypeDescriptors.strings()).via(s -> {
					return String.format("%tT %s %s", System.currentTimeMillis(), s.data,s.an_id);
				}))

				.apply(TextIO.write().to("output/msg").withoutSharding());

		p.run();
	}

	public static void writeCassandra() {
		FlinkPipelineOptions options = PipelineOptionsFactory.create().as(FlinkPipelineOptions.class);
		options.setRunner(FlinkRunner.class);
		options.setFilesToStage(Arrays.asList("target/test-bundled-0.1.jar"));
		options.setFlinkMaster("[local]]");
		options.setStreaming(false);
		Pipeline p = Pipeline.create(options);

		p.apply(Create.of(
				Arrays.asList("To be, or not to be: that is the question:", "Whether 'tis nobler in the mind to suffer",
						"The slings and arrows of fortune,", "Or to take arms against a sea of troubles,")))
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
				}))

				.apply(MapElements.into(TypeDescriptor.of(Riders.class)).via(s -> {
					return new Riders(String.format("%tT %s", System.currentTimeMillis(), s), UUID.randomUUID());
				}))

				.apply(CassandraIO.<Riders>write().withHosts(Arrays.asList("localhost")).withPort(9042)
						.withKeyspace("test").withEntity(Riders.class));

		p.run();
	}
}

/* To run cassandra locally in a container: */
/*
 * docker run -d --name cassandra --network host cassandra
 */

/* To create the schema - use `cqlsh` and run: */

/*
 CREATE KEYSPACE IF NOT EXISTS test WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
 */
/*
 CREATE TABLE IF NOT EXISTS test.test ( data text, an_id uuid, PRIMARY KEY(data) );
 */
