package org.apache.beam.examples;

import java.util.Arrays;

import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.runners.spark.SparkPipelineOptions;
import org.apache.beam.runners.spark.SparkRunner;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	public static PipelineOptions getPipelineOptions(boolean streaming) {
		String runner = System.getProperty("runner");
		switch (runner != null ? runner : "") {
		case "direct": {
			LOG.info("Using DirectRunner");
			DirectOptions options = PipelineOptionsFactory.create().as(DirectOptions.class);
			options.setRunner(DirectRunner.class);
			return options;
		}
		default:
		case "flink": {
			LOG.info("Using FlinkRunner");
			FlinkPipelineOptions options = PipelineOptionsFactory.create().as(FlinkPipelineOptions.class);
			options.setRunner(FlinkRunner.class);
			options.setFilesToStage(Arrays.asList("build/libs/beam-playground-0.1-all.jar"));
			options.setFlinkMaster("localhost");
			options.setStreaming(streaming);
			return options;
		}
		case "spark": {
			LOG.info("Using SparkRunner");
			SparkPipelineOptions options = PipelineOptionsFactory.create().as(SparkPipelineOptions.class);
			options.setRunner(SparkRunner.class);
			options.setFilesToStage(Arrays.asList("build/libs/beam-playground-0.1.jar"));
			options.setSparkMaster("local[1]");
			options.setStreaming(false);
			return options;
		}
		}
	}

	public static String getCassandraHosts() {
		return "localhost";
	}

	public static int getCassandraPort() {
		return 9042;
	}

	public static String getCassandraKeyspace() {
		return "test";
	}

	public static String getCassandraTable1() {
		return "table1";
	}

	public static String getCassandraTable2() {
		return "table2";
	}

	public static String getKafkaUrls() {
		return "localhost:9092";
		// return "broker.kafka.svc.cluster.local:9092";
	}

	public static String getKafkaTopic() {
		return "test_topic";
	}

	public static Object getKafkaConsumerGroupId() {
		return "test_grp_1";
	}
}
