package de.uni_koblenz.jgralab.impl.generic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.pcollections.POrderedSet;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.NoSuchAttributeException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.EdgeIterable;
import de.uni_koblenz.jgralab.impl.VertexIterable;
import de.uni_koblenz.jgralab.impl.std.GraphImpl;
import de.uni_koblenz.jgralab.schema.*;

/**
 * 
 * @author Bernhard
 * 
 */
public class GenericGraphImpl extends GraphImpl {

	private GraphClass type;
	private Map<String, Object> attributes;
	private Map<VertexClass, Set<EdgeClass>> vcInEdgeCache;
	private Map<VertexClass, Set<EdgeClass>> vcOutEdgeCache;

	protected GenericGraphImpl(String id, GraphClass type) {
		super(id, type, 100, 100);
	}

	protected GenericGraphImpl(GraphClass type, String id, int vmax, int emax) {
		super(id, type, vmax, emax);
		this.type = type;
		if (type.getAttributeCount() > 0) {
			attributes = new HashMap<String, Object>();
			for (Attribute a : type.getAttributeList()) {
				attributes.put(a.getName(), null);
			}
			initializeAttributesWithDefaultValues();
		}
		vcOutEdgeCache = new HashMap<VertexClass, Set<EdgeClass>>();
		vcInEdgeCache = new HashMap<VertexClass, Set<EdgeClass>>();
	}

	/**
	 * Creates a new instance if a generic Graph. This method isn't supposed to
	 * be called manually. Use
	 * <code>Schema.createGraph(ImplementationType.Generic)</code> instead!
	 */
	public static Graph create(GraphClass type, String id, int vmax, int emax) {
		return new GenericGraphImpl(type, id, vmax, emax);
	}

	/**
	 * Creates a new {@link GenericVertexImpl} in the graph that conforms to a
	 * given {@Link VertexClass} from the Schema.
	 */
	@Override
	public <T extends Vertex> T createVertex(VertexClass vc) {
		return createVertex(vc, 0);
	}

	@SuppressWarnings("unchecked")
	public <T extends Vertex> T createVertex(VertexClass vc, int id) {
		try {
			return (T) new GenericVertexImpl(vc, id, this);
		} catch (Exception e) {
			if (e instanceof GraphException) {
				throw (GraphException) e;
			} else {
				throw new GraphException(
						"Error creating vertex of VertexClass " + vc);
			}
		}
	}

	/**
	 * Creates a new {@Link GenericEdgeImpl} in the Graph that conforms
	 * to a given {@link EdgeClass} from the Schema.
	 */
	@Override
	public <T extends Edge> T createEdge(EdgeClass ec, Vertex alpha,
			Vertex omega) {
		return createEdge(ec, 0, alpha, omega);
	}

	@SuppressWarnings("unchecked")
	public <T extends Edge> T createEdge(EdgeClass ec, int id, Vertex alpha,
			Vertex omega) {
		try {
			return (T) new GenericEdgeImpl(ec, id, this, alpha, omega);
		} catch (Exception e) {
			if (e instanceof GraphException) {
				throw (GraphException) e;
			} else {
				throw new GraphException("Error creating edge of EdgeClass "
						+ ec);
			}
		}
	}

	@Override
	public AttributedElementClass getAttributedElementClass() {
		return type;
	}

	@Override
	public void readAttributeValueFromString(String attributeName, String value)
			throws GraphIOException, NoSuchAttributeException {
		if (attributes != null && attributes.containsKey(attributeName)) {
			attributes.put(attributeName, GenericUtil.parseGenericAttribute(
					type.getAttribute(attributeName).getDomain(),
					GraphIO.createStringReader(value, getSchema())));
		} else {
			throw new NoSuchAttributeException(
					"DefaultValueTestGraph doesn't contain an attribute "
							+ attributeName);
		}
	}

	@Override
	public void readAttributeValues(GraphIO io) throws GraphIOException {
		for (Attribute a : type.getAttributeList()) {
			attributes.put(a.getName(),
					GenericUtil.parseGenericAttribute(a.getDomain(), io));
		}
	}

	@Override
	public String writeAttributeValueToString(String attributeName)
			throws IOException, GraphIOException, NoSuchAttributeException {
		GraphIO io = GraphIO.createStringWriter(getSchema());
		GenericUtil.serializeGenericAttribute(io,
				type.getAttribute(attributeName).getDomain(),
				getAttribute(attributeName));
		return io.getStringWriterResult();
	}

	@Override
	public void writeAttributeValues(GraphIO io) throws IOException,
			GraphIOException {
		for (Attribute a : type.getAttributeList()) {
			GenericUtil.serializeGenericAttribute(io, a.getDomain(),
					attributes.get(a.getName()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(String name) throws NoSuchAttributeException {
		if (attributes == null || !attributes.containsKey(name)) {
			throw new NoSuchAttributeException(type.getSimpleName()
					+ " doesn't contain an attribute " + name);
		} else {
			return (T) attributes.get(name);
		}
	}

	@Override
	public <T> void setAttribute(String name, T data)
			throws NoSuchAttributeException {
		if (attributes == null || !attributes.containsKey(name)) {
			throw new NoSuchAttributeException(type.getSimpleName()
					+ " doesn't contain an attribute " + name);
		} else {
			try {
				if (!GenericUtil.conformsToDomain(data, type.getAttribute(name)
						.getDomain())) {
					throw new ClassCastException();
				} else {
					attributes.put(name, data);
				}
			} catch (ClassNotFoundException e) {
				System.err
						.println(type.getAttribute(name).getDomain()
								+ ".getJavaClassName(String schemaRootPackagePrefix) returned an unknown class name.");
				e.printStackTrace();
			}
		}

	}

	/**
	 * Checks the allowed outgoing {@link EdgeClass}es of a {@link VertexClass}.
	 * If this method is called for a <code>VertexClass</code> for the first
	 * time, the allowed outgoing <code>EdgeClass</code>es will be cached.
	 */
	public boolean cachedIsValidAlpha(VertexClass vc, EdgeClass ec) {
		if (!vcOutEdgeCache.containsKey(vc)) {
			vcOutEdgeCache.put(vc, vc.getValidFromEdgeClasses());
		}
		return vcOutEdgeCache.get(vc).contains(ec);
	}

	/**
	 * Checks the allowed incoming {@link EdgeClass}es of a {@link VertexClass}.
	 * If this method is called for a <code>VertexClass</code> for the first
	 * time, the allowed incoming <code>EdgeClass</code>es will be cached.
	 */
	public boolean cachedIsValidOmega(VertexClass vc, EdgeClass ec) {
		if (!vcInEdgeCache.containsKey(vc)) {
			vcInEdgeCache.put(vc, vc.getValidToEdgeClasses());
		}
		return vcInEdgeCache.get(vc).contains(ec);
	}

	@Override
	public Vertex getFirstVertex(VertexClass vertexClass) {
		Vertex v = getFirstVertex();
		if (v == null) {
			return null;
		}
		if (v.getAttributedElementClass().equals(vertexClass)) {
			return v;
		}
		return v.getNextVertex(vertexClass);
	}

	@Override
	public Edge getFirstEdge(EdgeClass edgeClass) {
		Edge e = getFirstEdge();
		if (e == null) {
			return null;
		}
		if (e.getAttributedElementClass().equals(edgeClass)) {
			return e;
		}
		return e.getNextEdge(edgeClass);
	}

	@Override
	public Iterable<Vertex> vertices(VertexClass vc) {
		return new VertexIterable<Vertex>(this, vc);
	}

	@Override
	public Iterable<Edge> edges(EdgeClass ec) {
		return new EdgeIterable<Edge>(this, ec);
	}

	@Override
	public void initializeAttributesWithDefaultValues() {
		for (Attribute attr : getAttributedElementClass().getAttributeList()) {
			if ((attr.getDefaultValueAsString() != null)
					&& !attr.getDefaultValueAsString().isEmpty()) {
				try {
					internalSetDefaultValue(attr);
				} catch (GraphIOException e) {
					e.printStackTrace();
				}
			} else {
				setAttribute(attr.getName(),
						GenericUtil.genericAttributeDefaultValue(attr
								.getDomain()));
			}
		}
	}

	// ************** unsupported methods ***************/
	@Override
	public Class<? extends AttributedElement> getSchemaClass() {
		throw new UnsupportedOperationException(
				"This method is not supported by the generic implementation");
	}

	@Override
	public Vertex getFirstVertex(Class<? extends Vertex> vertexClass) {
		throw new UnsupportedOperationException(
				"This method is not supported by the generic implementation");
	}

	@Override
	public Iterable<Vertex> vertices(Class<? extends Vertex> vertexClass) {
		throw new UnsupportedOperationException(
				"This method is not supported by the generic implementation");
	}

	@Override
	public Edge getFirstEdge(Class<? extends Edge> edgeClass) {
		throw new UnsupportedOperationException(
				"This method is not supported by the generic implementation");
	}

	@Override
	public Iterable<Edge> edges(Class<? extends Edge> edgeClass) {
		throw new UnsupportedOperationException(
				"This method is not supported by the generic implementation");
	}

	@Override
	public <T extends Vertex> POrderedSet<T> reachableVertices(
			Vertex startVertex, String pathDescription, Class<T> vertexType) {
		throw new UnsupportedOperationException(
				"This method is not supported by the generic implementation");
	}
}