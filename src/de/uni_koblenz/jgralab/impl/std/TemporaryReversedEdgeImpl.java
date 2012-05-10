package de.uni_koblenz.jgralab.impl.std;

import java.io.IOException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.TemporaryEdge;
import de.uni_koblenz.jgralab.impl.EdgeBaseImpl;
import de.uni_koblenz.jgralab.schema.EdgeClass;

public class TemporaryReversedEdgeImpl extends ReversedEdgeImpl implements
		TemporaryEdge {

	protected TemporaryReversedEdgeImpl(EdgeBaseImpl normalEdge, Graph graph) {
		super(normalEdge, graph);
	}

	@Override
	public EdgeClass getAttributedElementClass() {
		return this.graph.getGraphClass().getTemporaryEdgeClass();
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		throw new UnsupportedOperationException();

	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Edge convertToRealGraphElement(EdgeClass edgeClass) {
		return ((TemporaryEdge) this.getNormalEdge())
				.convertToRealGraphElement(edgeClass);
	}

	@Override
	public void deleteAttribute(String name) {
		((TemporaryEdge) this.getNormalEdge()).deleteAttribute(name);

	}

	@Override
	public boolean isTemporary() {
		return true;
	}
}