/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl;

import de.uni_koblenz.jgralab.impl.array.ReversedEdgeImpl;
import de.uni_koblenz.jgralab.impl.array.EdgeImpl;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.HasBaseDomainM2;

import java.io.IOException;

public class ReversedHasBaseDomainM2Impl extends ReversedEdgeImpl implements Edge, HasBaseDomainM2 {

	ReversedHasBaseDomainM2Impl(EdgeImpl e, Graph g) {
		super(e, g);
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		return ((HasBaseDomainM2)normalEdge).getAttribute(attributeName);
	}

	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		((HasBaseDomainM2)normalEdge).setAttribute(attributeName, data);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public HasBaseDomainM2 getNextHasBaseDomainM2InGraph() {
		return ((HasBaseDomainM2)normalEdge).getNextHasBaseDomainM2InGraph();
	}

	public HasBaseDomainM2 getNextHasBaseDomainM2InGraph(boolean noSubClasses) {
		return ((HasBaseDomainM2)normalEdge).getNextHasBaseDomainM2InGraph(noSubClasses);
	}

	public HasBaseDomainM2 getNextHasBaseDomainM2() {
		return (HasBaseDomainM2)getNextEdgeOfClass(HasBaseDomainM2.class);
	}

	public HasBaseDomainM2 getNextHasBaseDomainM2(EdgeDirection orientation) {
		return (HasBaseDomainM2)getNextEdgeOfClass(HasBaseDomainM2.class, orientation);
	}

	public HasBaseDomainM2 getNextHasBaseDomainM2(boolean noSubClasses) {
		return (HasBaseDomainM2)getNextEdgeOfClass(HasBaseDomainM2.class, noSubClasses);
	}

	public HasBaseDomainM2 getNextHasBaseDomainM2(EdgeDirection orientation, boolean noSubClasses) {
		return (HasBaseDomainM2)getNextEdgeOfClass(HasBaseDomainM2.class, orientation, noSubClasses);
	}

}
