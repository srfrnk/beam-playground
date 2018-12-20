package org.apache.beam.examples;

import java.util.Arrays;

import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

public class Config {
	public static PipelineOptions getPipelineOptions() {
		FlinkPipelineOptions options = PipelineOptionsFactory.create().as(FlinkPipelineOptions.class);
		options.setRunner(FlinkRunner.class);
		options.setFilesToStage(Arrays.asList("build/libs/beam-playground-0.1.jar"));
		options.setFlinkMaster("[local]");
		options.setStreaming(false);
		return options;
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
}
