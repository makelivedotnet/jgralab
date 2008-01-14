/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl;

import de.uni_koblenz.jgralab.impl.array.ReversedCompositionImpl;
import de.uni_koblenz.jgralab.impl.array.EdgeImpl;

import de.uni_koblenz.jgralab.Composition;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.ContainsGraphClassM2;

import java.io.IOException;

public class ReversedContainsGraphClassM2Impl extends ReversedCompositionImpl implements Composition, ContainsGraphClassM2 {

	ReversedContainsGraphClassM2Impl(EdgeImpl e, Graph g) {
		super(e, g);
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		return ((ContainsGraphClassM2)normalEdge).getAttribute(attributeName);
	}

	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		((ContainsGraphClassM2)normalEdge).setAttribute(attributeName, data);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public ContainsGraphClassM2 getNextContainsGraphClassM2InGraph() {
		return ((ContainsGraphClassM2)normalEdge).getNextContainsGraphClassM2InGraph();
	}

	public ContainsGraphClassM2 getNextContainsGraphClassM2InGraph(boolean noSubClasses) {
		return ((ContainsGraphClassM2)normalEdge).getNextContainsGraphClassM2InGraph(noSubClasses);
	}

	public ContainsGraphClassM2 getNextContainsGraphClassM2() {
		return (ContainsGraphClassM2)getNextEdgeOfClass(ContainsGraphClassM2.class);
	}

	public ContainsGraphClassM2 getNextContainsGraphClassM2(EdgeDirection orientation) {
		return (ContainsGraphClassM2)getNextEdgeOfClass(ContainsGraphClassM2.class, orientation);
	}

	public ContainsGraphClassM2 getNextContainsGraphClassM2(boolean noSubClasses) {
		return (ContainsGraphClassM2)getNextEdgeOfClass(ContainsGraphClassM2.class, noSubClasses);
	}

	public ContainsGraphClassM2 getNextContainsGraphClassM2(EdgeDirection orientation, boolean noSubClasses) {
		return (ContainsGraphClassM2)getNextEdgeOfClass(ContainsGraphClassM2.class, orientation, noSubClasses);
	}

}
