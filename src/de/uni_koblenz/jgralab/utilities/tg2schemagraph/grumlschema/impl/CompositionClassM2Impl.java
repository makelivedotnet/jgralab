/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl;

import de.uni_koblenz.jgralab.impl.array.VertexImpl;
import de.uni_koblenz.jgralab.impl.IncidenceIterable;

import de.uni_koblenz.jgralab.VertexClass;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.EdgeVertexPair;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.AggregationClassM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.AttributedElementClassM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.CompositionClassM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.ContainsGraphElementClassM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.EdgeClassM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.FromM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.GraphElementClassM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.HasAttributeM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.IsSubClassOfM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.IsSubEdgeClassOfM2;
import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.ToM2;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CompositionClassM2Impl extends VertexImpl implements AggregationClassM2, AttributedElementClassM2, CompositionClassM2, EdgeClassM2, GraphElementClassM2, Vertex {

	protected boolean aggregateFrom;

	protected boolean isAbstract;

	protected String name;

	public CompositionClassM2Impl(int id, Graph g) {
		super(id, g, (VertexClass)g.getGraphClass().getGraphElementClass("CompositionClassM2"));
	}

	public Class<? extends AttributedElement> getM1Class() {
		return CompositionClassM2.class;
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		if (attributeName.equals("aggregateFrom")) return aggregateFrom;
		if (attributeName.equals("isAbstract")) return isAbstract;
		if (attributeName.equals("name")) return name;
		throw new NoSuchFieldException("CompositionClassM2 doesn't contain an attribute " + attributeName);
	}

	@SuppressWarnings("unchecked")
	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		if (attributeName.equals("aggregateFrom")) {
			setAggregateFrom((Boolean) data);
			return;
		}
		if (attributeName.equals("isAbstract")) {
			setIsAbstract((Boolean) data);
			return;
		}
		if (attributeName.equals("name")) {
			setName((String) data);
			return;
		}
		throw new NoSuchFieldException("CompositionClassM2 doesn't contain an attribute " + attributeName);
	}

	public boolean isAggregateFrom() {
		return aggregateFrom;
	}

	public boolean isIsAbstract() {
		return isAbstract;
	}

	public String getName() {
		return name;
	}

	public void setAggregateFrom(boolean aggregateFrom) {
		this.aggregateFrom = aggregateFrom;
		modified();
	}

	public void setIsAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
		modified();
	}

	public void setName(String name) {
		this.name = name;
		modified();
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
		aggregateFrom = io.matchBoolean();
		setAggregateFrom(aggregateFrom);
		isAbstract = io.matchBoolean();
		setIsAbstract(isAbstract);
		name = io.matchUtfString();
		setName(name);
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
		io.space();
		io.writeBoolean(aggregateFrom);
		io.writeBoolean(isAbstract);
		io.writeUtfString(name);
	}

	/* add all valid from edges */
	private static Set<Class<? extends Edge>> validFromEdges = new HashSet<Class<? extends Edge>>();
	
	/* (non-Javadoc)
	 * @see jgralab.Vertex:isValidAlpha()
	 */
	public boolean isValidAlpha(Edge edge) {
		return validFromEdges.contains(edge.getClass());
	}
	
	{

		validFromEdges.add(FromM2Impl.class);

		validFromEdges.add(ToM2Impl.class);

		validFromEdges.add(IsSubEdgeClassOfM2Impl.class);

		validFromEdges.add(ContainsGraphElementClassM2Impl.class);

	}
	
	/* add all valid to edges */
	private static Set<Class<? extends Edge>> validToEdges = new HashSet<Class<? extends Edge>>();
	
	/* (non-Javadoc)
	 * @see jgralab.Vertex:isValidOemga()
	 */
	public boolean isValidOmega(Edge edge) {
		return validToEdges.contains(edge.getClass());
	}
	
	{

		validToEdges.add(IsSubEdgeClassOfM2Impl.class);

		validToEdges.add(HasAttributeM2Impl.class);

	}

	public AggregationClassM2 getNextAggregationClassM2() {
		return (AggregationClassM2)getNextVertexOfClass(AggregationClassM2.class);
	}

	public AggregationClassM2 getNextAggregationClassM2(boolean noSubClasses) {
		return (AggregationClassM2)getNextVertexOfClass(AggregationClassM2.class, noSubClasses);
	}

	public AttributedElementClassM2 getNextAttributedElementClassM2() {
		return (AttributedElementClassM2)getNextVertexOfClass(AttributedElementClassM2.class);
	}

	public CompositionClassM2 getNextCompositionClassM2() {
		return (CompositionClassM2)getNextVertexOfClass(CompositionClassM2.class);
	}

	public CompositionClassM2 getNextCompositionClassM2(boolean noSubClasses) {
		return (CompositionClassM2)getNextVertexOfClass(CompositionClassM2.class, noSubClasses);
	}

	public EdgeClassM2 getNextEdgeClassM2() {
		return (EdgeClassM2)getNextVertexOfClass(EdgeClassM2.class);
	}

	public EdgeClassM2 getNextEdgeClassM2(boolean noSubClasses) {
		return (EdgeClassM2)getNextVertexOfClass(EdgeClassM2.class, noSubClasses);
	}

	public GraphElementClassM2 getNextGraphElementClassM2() {
		return (GraphElementClassM2)getNextVertexOfClass(GraphElementClassM2.class);
	}

	public FromM2 getFirstFromM2() {
		return (FromM2)getFirstEdgeOfClass(FromM2.class);
	}

	public FromM2 getFirstFromM2(EdgeDirection orientation) {
		return (FromM2)getFirstEdgeOfClass(FromM2.class, orientation);
	}

	public FromM2 getFirstFromM2(boolean noSubClasses) {
		return (FromM2)getFirstEdgeOfClass(FromM2.class, noSubClasses);
	}

	public FromM2 getFirstFromM2(EdgeDirection orientation, boolean noSubClasses) {
		return (FromM2)getFirstEdgeOfClass(FromM2.class, orientation, noSubClasses);
	}

	public ToM2 getFirstToM2() {
		return (ToM2)getFirstEdgeOfClass(ToM2.class);
	}

	public ToM2 getFirstToM2(EdgeDirection orientation) {
		return (ToM2)getFirstEdgeOfClass(ToM2.class, orientation);
	}

	public ToM2 getFirstToM2(boolean noSubClasses) {
		return (ToM2)getFirstEdgeOfClass(ToM2.class, noSubClasses);
	}

	public ToM2 getFirstToM2(EdgeDirection orientation, boolean noSubClasses) {
		return (ToM2)getFirstEdgeOfClass(ToM2.class, orientation, noSubClasses);
	}

	public HasAttributeM2 getFirstHasAttributeM2() {
		return (HasAttributeM2)getFirstEdgeOfClass(HasAttributeM2.class);
	}

	public HasAttributeM2 getFirstHasAttributeM2(EdgeDirection orientation) {
		return (HasAttributeM2)getFirstEdgeOfClass(HasAttributeM2.class, orientation);
	}

	public HasAttributeM2 getFirstHasAttributeM2(boolean noSubClasses) {
		return (HasAttributeM2)getFirstEdgeOfClass(HasAttributeM2.class, noSubClasses);
	}

	public HasAttributeM2 getFirstHasAttributeM2(EdgeDirection orientation, boolean noSubClasses) {
		return (HasAttributeM2)getFirstEdgeOfClass(HasAttributeM2.class, orientation, noSubClasses);
	}

	public IsSubEdgeClassOfM2 getFirstIsSubEdgeClassOfM2() {
		return (IsSubEdgeClassOfM2)getFirstEdgeOfClass(IsSubEdgeClassOfM2.class);
	}

	public IsSubEdgeClassOfM2 getFirstIsSubEdgeClassOfM2(EdgeDirection orientation) {
		return (IsSubEdgeClassOfM2)getFirstEdgeOfClass(IsSubEdgeClassOfM2.class, orientation);
	}

	public IsSubEdgeClassOfM2 getFirstIsSubEdgeClassOfM2(boolean noSubClasses) {
		return (IsSubEdgeClassOfM2)getFirstEdgeOfClass(IsSubEdgeClassOfM2.class, noSubClasses);
	}

	public IsSubEdgeClassOfM2 getFirstIsSubEdgeClassOfM2(EdgeDirection orientation, boolean noSubClasses) {
		return (IsSubEdgeClassOfM2)getFirstEdgeOfClass(IsSubEdgeClassOfM2.class, orientation, noSubClasses);
	}

	public ContainsGraphElementClassM2 getFirstContainsGraphElementClassM2() {
		return (ContainsGraphElementClassM2)getFirstEdgeOfClass(ContainsGraphElementClassM2.class);
	}

	public ContainsGraphElementClassM2 getFirstContainsGraphElementClassM2(EdgeDirection orientation) {
		return (ContainsGraphElementClassM2)getFirstEdgeOfClass(ContainsGraphElementClassM2.class, orientation);
	}

	public ContainsGraphElementClassM2 getFirstContainsGraphElementClassM2(boolean noSubClasses) {
		return (ContainsGraphElementClassM2)getFirstEdgeOfClass(ContainsGraphElementClassM2.class, noSubClasses);
	}

	public ContainsGraphElementClassM2 getFirstContainsGraphElementClassM2(EdgeDirection orientation, boolean noSubClasses) {
		return (ContainsGraphElementClassM2)getFirstEdgeOfClass(ContainsGraphElementClassM2.class, orientation, noSubClasses);
	}

	public IsSubClassOfM2 getFirstIsSubClassOfM2() {
		return (IsSubClassOfM2)getFirstEdgeOfClass(IsSubClassOfM2.class);
	}

	public IsSubClassOfM2 getFirstIsSubClassOfM2(EdgeDirection orientation) {
		return (IsSubClassOfM2)getFirstEdgeOfClass(IsSubClassOfM2.class, orientation);
	}

	public Iterable<EdgeVertexPair<? extends FromM2, ? extends Vertex>> getFromM2Incidences() {
		return new IncidenceIterable<FromM2, Vertex>(this, FromM2.class);
	}
	
	public Iterable<EdgeVertexPair<? extends FromM2, ? extends Vertex>> getFromM2Incidences(boolean noSubClasses) {
		return new IncidenceIterable<FromM2, Vertex>(this, FromM2.class, noSubClasses);
	}

	public Iterable<EdgeVertexPair<? extends FromM2, ? extends Vertex>> getFromM2Incidences(EdgeDirection direction, boolean noSubClasses) {
		return  new IncidenceIterable<FromM2, Vertex>(this, FromM2.class, direction, noSubClasses);
	}
	
	public Iterable<EdgeVertexPair<? extends FromM2, ? extends Vertex>> getFromM2Incidences(EdgeDirection direction) {
		return new IncidenceIterable<FromM2, Vertex>(this, FromM2.class, direction);
	}

	public Iterable<EdgeVertexPair<? extends ToM2, ? extends Vertex>> getToM2Incidences() {
		return new IncidenceIterable<ToM2, Vertex>(this, ToM2.class);
	}
	
	public Iterable<EdgeVertexPair<? extends ToM2, ? extends Vertex>> getToM2Incidences(boolean noSubClasses) {
		return new IncidenceIterable<ToM2, Vertex>(this, ToM2.class, noSubClasses);
	}

	public Iterable<EdgeVertexPair<? extends ToM2, ? extends Vertex>> getToM2Incidences(EdgeDirection direction, boolean noSubClasses) {
		return  new IncidenceIterable<ToM2, Vertex>(this, ToM2.class, direction, noSubClasses);
	}
	
	public Iterable<EdgeVertexPair<? extends ToM2, ? extends Vertex>> getToM2Incidences(EdgeDirection direction) {
		return new IncidenceIterable<ToM2, Vertex>(this, ToM2.class, direction);
	}

	public Iterable<EdgeVertexPair<? extends HasAttributeM2, ? extends Vertex>> getHasAttributeM2Incidences() {
		return new IncidenceIterable<HasAttributeM2, Vertex>(this, HasAttributeM2.class);
	}
	
	public Iterable<EdgeVertexPair<? extends HasAttributeM2, ? extends Vertex>> getHasAttributeM2Incidences(boolean noSubClasses) {
		return new IncidenceIterable<HasAttributeM2, Vertex>(this, HasAttributeM2.class, noSubClasses);
	}

	public Iterable<EdgeVertexPair<? extends HasAttributeM2, ? extends Vertex>> getHasAttributeM2Incidences(EdgeDirection direction, boolean noSubClasses) {
		return  new IncidenceIterable<HasAttributeM2, Vertex>(this, HasAttributeM2.class, direction, noSubClasses);
	}
	
	public Iterable<EdgeVertexPair<? extends HasAttributeM2, ? extends Vertex>> getHasAttributeM2Incidences(EdgeDirection direction) {
		return new IncidenceIterable<HasAttributeM2, Vertex>(this, HasAttributeM2.class, direction);
	}

	public Iterable<EdgeVertexPair<? extends IsSubEdgeClassOfM2, ? extends Vertex>> getIsSubEdgeClassOfM2Incidences() {
		return new IncidenceIterable<IsSubEdgeClassOfM2, Vertex>(this, IsSubEdgeClassOfM2.class);
	}
	
	public Iterable<EdgeVertexPair<? extends IsSubEdgeClassOfM2, ? extends Vertex>> getIsSubEdgeClassOfM2Incidences(boolean noSubClasses) {
		return new IncidenceIterable<IsSubEdgeClassOfM2, Vertex>(this, IsSubEdgeClassOfM2.class, noSubClasses);
	}

	public Iterable<EdgeVertexPair<? extends IsSubEdgeClassOfM2, ? extends Vertex>> getIsSubEdgeClassOfM2Incidences(EdgeDirection direction, boolean noSubClasses) {
		return  new IncidenceIterable<IsSubEdgeClassOfM2, Vertex>(this, IsSubEdgeClassOfM2.class, direction, noSubClasses);
	}
	
	public Iterable<EdgeVertexPair<? extends IsSubEdgeClassOfM2, ? extends Vertex>> getIsSubEdgeClassOfM2Incidences(EdgeDirection direction) {
		return new IncidenceIterable<IsSubEdgeClassOfM2, Vertex>(this, IsSubEdgeClassOfM2.class, direction);
	}

	public Iterable<EdgeVertexPair<? extends ContainsGraphElementClassM2, ? extends Vertex>> getContainsGraphElementClassM2Incidences() {
		return new IncidenceIterable<ContainsGraphElementClassM2, Vertex>(this, ContainsGraphElementClassM2.class);
	}
	
	public Iterable<EdgeVertexPair<? extends ContainsGraphElementClassM2, ? extends Vertex>> getContainsGraphElementClassM2Incidences(boolean noSubClasses) {
		return new IncidenceIterable<ContainsGraphElementClassM2, Vertex>(this, ContainsGraphElementClassM2.class, noSubClasses);
	}

	public Iterable<EdgeVertexPair<? extends ContainsGraphElementClassM2, ? extends Vertex>> getContainsGraphElementClassM2Incidences(EdgeDirection direction, boolean noSubClasses) {
		return  new IncidenceIterable<ContainsGraphElementClassM2, Vertex>(this, ContainsGraphElementClassM2.class, direction, noSubClasses);
	}
	
	public Iterable<EdgeVertexPair<? extends ContainsGraphElementClassM2, ? extends Vertex>> getContainsGraphElementClassM2Incidences(EdgeDirection direction) {
		return new IncidenceIterable<ContainsGraphElementClassM2, Vertex>(this, ContainsGraphElementClassM2.class, direction);
	}

	public Iterable<EdgeVertexPair<? extends IsSubClassOfM2, ? extends Vertex>> getIsSubClassOfM2Incidences() {
		return new IncidenceIterable<IsSubClassOfM2, Vertex>(this, IsSubClassOfM2.class);
	}
	
	public Iterable<EdgeVertexPair<? extends IsSubClassOfM2, ? extends Vertex>> getIsSubClassOfM2Incidences(boolean noSubClasses) {
		return new IncidenceIterable<IsSubClassOfM2, Vertex>(this, IsSubClassOfM2.class, noSubClasses);
	}

	public Iterable<EdgeVertexPair<? extends IsSubClassOfM2, ? extends Vertex>> getIsSubClassOfM2Incidences(EdgeDirection direction, boolean noSubClasses) {
		return  new IncidenceIterable<IsSubClassOfM2, Vertex>(this, IsSubClassOfM2.class, direction, noSubClasses);
	}
	
	public Iterable<EdgeVertexPair<? extends IsSubClassOfM2, ? extends Vertex>> getIsSubClassOfM2Incidences(EdgeDirection direction) {
		return new IncidenceIterable<IsSubClassOfM2, Vertex>(this, IsSubClassOfM2.class, direction);
	}

}
