package org.apache.beam.examples;

import java.io.Serializable;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "test", name = "test", readConsistency = "ANY", writeConsistency = "ANY", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class Riders implements Serializable {
	private static final long serialVersionUID = 1L;

	@PartitionKey
	@Column(name = "data")
	public String data;

	@Column(name = "an_id")
	public UUID an_id;

	public Riders() {
	}

	public Riders(String data, UUID an_id) {
		this.data=data;
		this.an_id=an_id;
	}
}
