package de.uni_koblenz.jgralab.greql2.types;

import java.io.IOException;

import org.pcollections.ArrayPMap;
import org.pcollections.PMap;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

public class Record implements de.uni_koblenz.jgralab.Record {
	private PMap<String, Object> entries;

	private Record() {
		entries = ArrayPMap.empty();
	}

	private Record(PMap<String, Object> m) {
		entries = m;
	}

	private static Record empty = new Record();

	public static Record empty() {
		return empty;
	}

	public Record plus(String name, Object value) {
		return new Record(entries.plus(name, value));
	}

	@Override
	public Object getComponent(String name) {
		return entries.get(name);
	}

	@Override
	public void writeComponentValues(GraphIO io) throws IOException,
			GraphIOException {
		throw new UnsupportedOperationException();
	}
}
