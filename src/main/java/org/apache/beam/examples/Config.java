package org.apache.beam.examples;

import java.util.Arrays;

import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.FlinkRunner;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	public static PipelineOptions getPipelineOptions() {
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
			options.setFilesToStage(Arrays.asList("build/libs/beam-playground-0.1.jar"));
			options.setFlinkMaster("[local]");
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
}
