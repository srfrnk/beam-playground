package org.apache.beam.examples;

import java.util.Arrays;

import com.google.auto.value.AutoValue;

import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
// import org.apache.beam.runners.spark.SparkPipelineOptions;
// import org.apache.beam.runners.spark.SparkRunner;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AutoValue
public abstract class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	public static Config create(boolean streaming) {
		Builder builder = new AutoValue_Config.Builder();

		String runner = System.getProperty("runner");
		switch (runner != null ? runner : "") {
		case "direct": {
			LOG.info("Using DirectRunner");
			DirectOptions pipelineOptions = PipelineOptionsFactory.create().as(DirectOptions.class);
			pipelineOptions.setRunner(DirectRunner.class);
			builder.setPipelineOptions(pipelineOptions).setCassandraHosts("localhost").setCassandraPort(9042)
					.setCassandraKeyspace("test").setCassandraTable1("table1").setCassandraTable2("table2")
					.setKafkaUrls("localhost:9092").setKafkaTopic("test_topic").setKafkaConsumerGroupId("test_grp_1")
					.setoutputPath("./output");
			break;
		}
		case "flink-local": {
			LOG.info("Using FlinkRunner Local");
			FlinkPipelineOptions pipelineOptions = PipelineOptionsFactory.create().as(FlinkPipelineOptions.class);
			pipelineOptions.setRunner(FlinkRunner.class);
			pipelineOptions.setFlinkMaster("[local]");
			pipelineOptions.setStreaming(streaming);
			builder.setPipelineOptions(pipelineOptions).setCassandraHosts("localhost").setCassandraPort(9042)
					.setCassandraKeyspace("test").setCassandraTable1("table1").setCassandraTable2("table2")
					.setKafkaUrls("localhost:9092").setKafkaTopic("test_topic").setKafkaConsumerGroupId("test_grp_1")
					.setoutputPath("./output");
			break;
		}
		default:
		case "flink-cluster": {
			LOG.info("Using FlinkRunner Cluster");
			FlinkPipelineOptions pipelineOptions = PipelineOptionsFactory.create().as(FlinkPipelineOptions.class);
			pipelineOptions.setRunner(FlinkRunner.class);
			pipelineOptions.setFlinkMaster("flink-jobmanager.flink:8081");
			pipelineOptions.setFilesToStage(Arrays.asList("./build/libs/beam-playground-0.1-all.jar"));
			pipelineOptions.setTempLocation("/tmp");
			// pipelineOptions.setStreaming(streaming);
			builder.setPipelineOptions(pipelineOptions).setCassandraHosts("cassandra.cassandra").setCassandraPort(9042)
					.setCassandraKeyspace("test").setCassandraTable1("table1").setCassandraTable2("table2")
					.setKafkaUrls("broker.kafka.svc.cluster.local:9092").setKafkaTopic("test_topic")
					.setKafkaConsumerGroupId("test_grp_1").setoutputPath("/tmp");
			break;
		}
		// case "spark": {
		// LOG.info("Using SparkRunner");
		// SparkPipelineOptions pipelineOptions =
		// PipelineOptionsFactory.create().as(SparkPipelineOptions.class);
		// pipelineOptions.setRunner(SparkRunner.class);
		// pipelineOptions.setFilesToStage(Arrays.asList("build/libs/beam-playground-0.1.jar"));
		// pipelineOptions.setSparkMaster("local[1]");
		// pipelineOptions.setStreaming(false);
		// builder.setPipelineOptions(pipelineOptions).setCassandraHosts("cassandra.cassandra").setCassandraPort(9042)
		// .setCassandraKeyspace("test").setCassandraTable1("table1").setCassandraTable2("table2")
		// .setKafkaUrls("broker.kafka.svc.cluster.local:9092").setKafkaTopic("test_topic")
		// .setKafkaConsumerGroupId("test_grp_1");
		// break;
		// }
		}
		return builder.build();
	}

	public abstract PipelineOptions pipelineOptions();

	public abstract String cassandraHosts();

	public abstract int cassandraPort();

	public abstract String cassandraKeyspace();

	public abstract String cassandraTable1();

	public abstract String cassandraTable2();

	public abstract String kafkaUrls();

	public abstract String kafkaTopic();

	public abstract String kafkaConsumerGroupId();

	public abstract String outputPath();

	@AutoValue.Builder
	abstract static class Builder {
		abstract Builder setPipelineOptions(PipelineOptions pipelineOptions);

		abstract Builder setCassandraHosts(String cassandraHosts);

		abstract Builder setCassandraPort(int cassandraPort);

		abstract Builder setCassandraKeyspace(String cassandraKeyspace);

		abstract Builder setCassandraTable1(String cassandraTable1);

		abstract Builder setCassandraTable2(String cassandraTable2);

		abstract Builder setKafkaUrls(String kafkaUrls);

		abstract Builder setKafkaTopic(String kafkaTopic);

		abstract Builder setKafkaConsumerGroupId(String kafkaConsumerGroupId);

		abstract Builder setoutputPath(String outputPath);

		abstract Config build();
	}
}
