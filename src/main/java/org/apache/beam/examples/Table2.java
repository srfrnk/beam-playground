package org.apache.beam.examples;

import java.io.Serializable;
import java.util.UUID;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "test", name = "table2", readConsistency = "ONE", writeConsistency = "ONE",
		caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class Table2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@PartitionKey
	@Column(name = "data")
	public String data;

	@Column(name = "an_id")
	public UUID an_id;

	@Column(name = "another_id")
	public UUID another_id;

	public Table2() {
	}

	public Table2(String data, UUID an_id, UUID another_id) {
		this.data = data;
		this.an_id = an_id;
		this.another_id = another_id;
	}

	@Override
	public boolean equals(Object obj) {
		Table2 other = (Table2) obj;
		return this.data.equals(other.data) && this.an_id.equals(other.an_id);
	}
}
