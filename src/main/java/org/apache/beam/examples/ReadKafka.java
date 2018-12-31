package org.apache.beam.examples;

import java.util.Arrays;

import com.google.common.collect.ImmutableMap;

import org.apache.beam.runners.flink.FlinkExecutionEnvironments;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Duration;

public class ReadKafka {
	public static void main(String[] args) {
		Pipeline p = Pipeline.create(Config.getPipelineOptions(true));

		p.apply(KafkaIO.<Long, String>read().withBootstrapServers(Config.getKafkaUrls())
				.withTopic(Config.getKafkaTopic()).withKeyDeserializer(LongDeserializer.class)
				.withValueDeserializer(StringDeserializer.class)
				.updateConsumerProperties(ImmutableMap.of("group.id", Config.getKafkaConsumerGroupId()))
				.withLogAppendTime().withReadCommitted().commitOffsetsInFinalize().withoutMetadata())

				.apply(Values.<String>create())

				.apply(Window.<String>into(FixedWindows.of(Duration.standardMinutes(1))))

				.apply(TextIO.write().to("output/from_table1").withWindowedWrites().withNumShards(1));

		p.run();
	}
}
