package org.apache.beam.examples;

import java.io.Serializable;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace="test", name = "table1", readConsistency = "ONE", writeConsistency = "ONE", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class Table1 implements Serializable {
	private static final long serialVersionUID = 1L;

	@PartitionKey
	@Column(name = "data")
	public String data;

	@Column(name = "an_id")
	public UUID an_id;

	public Table1() {
	}

	public Table1(String data, UUID an_id) {
		this.data = data;
		this.an_id = an_id;
	}

	@Override
	public boolean equals(Object obj) {
		Table1 other = (Table1) obj;
		return this.data.equals(other.data) && this.an_id.equals(other.an_id);
	}
}
