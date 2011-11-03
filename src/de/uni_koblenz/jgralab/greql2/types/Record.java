package de.uni_koblenz.jgralab.greql2.types;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.pcollections.ArrayPSet;
import org.pcollections.PMap;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.NoSuchAttributeException;

public class Record implements de.uni_koblenz.jgralab.Record {
	private PMap<String, Object> entries;

	private Record() {
		entries = JGraLab.map();
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
		if (entries.containsKey(name)) {
			return entries.get(name);
		}
		throw new NoSuchAttributeException(
				"Record doesn't contain a component '" + name + "'");
	}

	@Override
	public void writeComponentValues(GraphIO io) throws IOException,
			GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasComponent(String name) {
		return entries.containsKey(name);
	}

	@Override
	public List<String> getComponentNames() {
		return ((ArrayPSet<String>) entries.keySet()).toPVector();
	}

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (Object v : entries.values()) {
			h += v.hashCode();
		}
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Record) {
			return entries.equals(((Record) obj).entries);
		}
		if (obj instanceof de.uni_koblenz.jgralab.Record) {
			de.uni_koblenz.jgralab.Record r = (de.uni_koblenz.jgralab.Record) obj;
			if (size() != r.size()) {
				return false;
			}
			try {
				Iterator<Object> v = entries.values().iterator();
				for (String k : entries.keySet()) {
					if (!r.getComponent(k).equals(v.next())) {
						return false;
					}
				}
				return true;
			} catch (NoSuchAttributeException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public PMap<String, Object> toPMap() {
		return entries;
	}
}
