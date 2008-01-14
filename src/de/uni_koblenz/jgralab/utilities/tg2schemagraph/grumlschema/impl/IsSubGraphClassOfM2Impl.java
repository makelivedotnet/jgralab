/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl;

import de.uni_koblenz.jgralab.impl.array.EdgeImpl;

import de.uni_koblenz.jgralab.EdgeClass;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl.ReversedIsSubGraphClassOfM2Impl;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.IsSubClassOfM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.IsSubGraphClassOfM2;

import java.io.IOException;
/**
FromVertexClass: GraphClassM2
FromRoleName : 
ToVertexClass: GraphClassM2
toRoleName : 
 */

public class IsSubGraphClassOfM2Impl extends EdgeImpl implements Edge, IsSubClassOfM2, IsSubGraphClassOfM2 {

	public IsSubGraphClassOfM2Impl(int id, Graph g) {
		super(id, g, (EdgeClass)g.getGraphClass().getGraphElementClass("IsSubGraphClassOfM2"));
		reversedEdge = new ReversedIsSubGraphClassOfM2Impl(this, g);
	}

	public Class<? extends AttributedElement> getM1Class() {
		return IsSubGraphClassOfM2.class;
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		throw new NoSuchFieldException("IsSubGraphClassOfM2 doesn't contain an attribute " + attributeName);
	}

	@SuppressWarnings("unchecked")
	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		throw new NoSuchFieldException("IsSubGraphClassOfM2 doesn't contain an attribute " + attributeName);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public IsSubClassOfM2 getNextIsSubClassOfM2InGraph() {
		return (IsSubClassOfM2)getNextEdgeOfClassInGraph(IsSubClassOfM2.class);
	}

	public IsSubGraphClassOfM2 getNextIsSubGraphClassOfM2InGraph() {
		return (IsSubGraphClassOfM2)getNextEdgeOfClassInGraph(IsSubGraphClassOfM2.class);
	}

	public IsSubGraphClassOfM2 getNextIsSubGraphClassOfM2InGraph(boolean noSubClasses) {
		return (IsSubGraphClassOfM2)getNextEdgeOfClassInGraph(IsSubGraphClassOfM2.class, noSubClasses);
	}

	public IsSubClassOfM2 getNextIsSubClassOfM2() {
		return (IsSubClassOfM2)getNextEdgeOfClass(IsSubClassOfM2.class);
	}

	public IsSubClassOfM2 getNextIsSubClassOfM2(EdgeDirection orientation) {
		return (IsSubClassOfM2)getNextEdgeOfClass(IsSubClassOfM2.class, orientation);
	}

	public IsSubGraphClassOfM2 getNextIsSubGraphClassOfM2() {
		return (IsSubGraphClassOfM2)getNextEdgeOfClass(IsSubGraphClassOfM2.class);
	}

	public IsSubGraphClassOfM2 getNextIsSubGraphClassOfM2(EdgeDirection orientation) {
		return (IsSubGraphClassOfM2)getNextEdgeOfClass(IsSubGraphClassOfM2.class, orientation);
	}

	public IsSubGraphClassOfM2 getNextIsSubGraphClassOfM2(boolean noSubClasses) {
		return (IsSubGraphClassOfM2)getNextEdgeOfClass(IsSubGraphClassOfM2.class, noSubClasses);
	}

	public IsSubGraphClassOfM2 getNextIsSubGraphClassOfM2(EdgeDirection orientation, boolean noSubClasses) {
		return (IsSubGraphClassOfM2)getNextEdgeOfClass(IsSubGraphClassOfM2.class, orientation, noSubClasses);
	}

}
