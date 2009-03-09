package de.uni_koblenz.jgralab.jniserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import de.uni_koblenz.jgralab.Attribute;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.WorkInProgress;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Domain;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.QualifiedName;
import de.uni_koblenz.jgralab.schema.Schema;
import de.uni_koblenz.jgralab.schema.VertexClass;

/**
 * A JNI server class for calling JGraLab from C++.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
@WorkInProgress(description = "totally incomplete, mainly untested", responsibleDevelopers = "riediger")
public class JniServer {
	private static int keyGenerator;

	/**
	 * the {@code Map} holding the graphs created or loaded via the {@code
	 * JGraLabFacade}
	 */
	private Map<Integer, Graph> graphs;

	public JniServer() {
		keyGenerator = 1;
		graphs = new Hashtable<Integer, Graph>();
	}

	/**
	 * Stores {@code graph} and returns the graphId for accessing the graph.
	 * 
	 * @param graph
	 *            a graph
	 * @return handle for accessing a graph in the Map {@code graphs}
	 */
	private int addGraph(Graph graph) {
		if (graphs.containsValue(graph)) {
			throw new GraphException("Graph was already added before!");
		}
		int key = keyGenerator;
		++keyGenerator;
		graphs.put(key, graph);
		return key;
	}

	/**
	 * Removes the graph with id {@code graphId} from this JniServer.
	 * 
	 * @param graphId
	 *            the id of the graph to be deleted
	 */
	public void deleteGraph(int graphId) {
		graphs.remove(graphId);
	}

	/**
	 * Checks whether a graph with the handle {@code graphId} exists.
	 * 
	 * @param graphId
	 *            the handle for which the existence of a graph shall be checked
	 * @return {@code true} if a graph with handle {@code graphNo} exists,
	 *         {@code false} otherwise
	 */
	public boolean containsGraph(int graphId) {
		return graphs.containsKey(graphId);
	}

	public int createGraph(String schemaName, String graphClassName, int vMax,
			int eMax) {
		Class<?> schemaClass;
		try {
			schemaClass = Class.forName(schemaName);
			Schema schema = (Schema) (schemaClass.getMethod("instance",
					(Class[]) null).invoke(null));

			Method graphCreateMethod = schema
					.getGraphCreateMethod(new QualifiedName(graphClassName));

			Graph g = (Graph) (graphCreateMethod.invoke(null, new Object[] {
					null, vMax, eMax }));
			return addGraph(g);
		} catch (Exception e) {
			throw new GraphException("Exception while creating graph.", e);
		}
	}

	public void saveGraph(int graphId, String fileName) {
		try {
			GraphIO.saveGraphToFile(fileName, graphs.get(graphId), null);
		} catch (Exception e) {
			throw new GraphException("Exception while saving graph.", e);
		}
	}

	public int loadGraph(String fileName) {
		try {
			Graph g = GraphIO.loadGraphFromFile(fileName, null);
			return addGraph(g);
		} catch (Exception e) {
			throw new GraphException("Exception while loading graph.", e);
		}
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------

	public int createVertex(int graphId, String vertexClassName) {
		Graph graph = graphs.get(graphId);
		Class<? extends Vertex> m1Class = graph.getGraphClass().getVertexClass(
				new QualifiedName(vertexClassName)).getM1Class();
		return graph.createVertex(m1Class).getId();
	}

	public void setVertexAttribute(int graphId, int vertexId,
			String attributeName, boolean value) {
		setAttribute(graphs.get(graphId).getVertex(vertexId), attributeName,
				value);
	}

	public void setVertexAttribute(int graphId, int vertexId,
			String attributeName, int value) {
		setAttribute(graphs.get(graphId).getVertex(vertexId), attributeName,
				value);
	}

	public void setVertexAttribute(int graphId, int vertexId,
			String attributeName, long value) {
		setAttribute(graphs.get(graphId).getVertex(vertexId), attributeName,
				value);
	}

	public void setVertexAttribute(int graphId, int vertexId,
			String attributeName, double value) {
		setAttribute(graphs.get(graphId).getVertex(vertexId), attributeName,
				value);
	}

	public void setVertexAttribute(int graphId, int vertexId,
			String attributeName, String value) {
		setAttribute(graphs.get(graphId).getVertex(vertexId), attributeName,
				value);
	}

	public void setVertexEnumAttribute(int graphId, int vertexId,
			String attributeName, String value) {
		setEnumAttribute(graphs.get(graphId).getVertex(vertexId),
				attributeName, value);
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------

	public int createEdge(int graphId, String edgeClassName, int alphaId,
			int omegaId) {
		Graph graph = graphs.get(graphId);
		Class<? extends Edge> m1Class = graph.getGraphClass().getEdgeClass(
				new QualifiedName(edgeClassName)).getM1Class();
		return graph.createEdge(m1Class, graph.getVertex(alphaId),
				graph.getVertex(omegaId)).getId();
	}

	public void setEdgeAttribute(int graphId, int edgeId, String attributeName,
			boolean value) {
		setAttribute(graphs.get(graphId).getEdge(edgeId), attributeName, value);
	}

	public void setEdgeAttribute(int graphId, int edgeId, String attributeName,
			int value) {
		setAttribute(graphs.get(graphId).getEdge(edgeId), attributeName, value);
	}

	public void setEdgeAttribute(int graphId, int edgeId, String attributeName,
			long value) {
		setAttribute(graphs.get(graphId).getEdge(edgeId), attributeName, value);
	}

	public void setEdgeAttribute(int graphId, int edgeId, String attributeName,
			double value) {
		setAttribute(graphs.get(graphId).getEdge(edgeId), attributeName, value);
	}

	public void setEdgeAttribute(int graphId, int edgeId, String attributeName,
			String value) {
		setAttribute(graphs.get(graphId).getEdge(edgeId), attributeName, value);
	}

	public void setEdgeEnumAttribute(int graphId, int edgeId,
			String attributeName, String value) {
		setEnumAttribute(graphs.get(graphId).getEdge(edgeId), attributeName,
				value);
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------

	public int getFirstVertex(int graphId, String vertexClassName) {
		Graph g = graphs.get(graphId);
		Vertex v = (vertexClassName != null) ? g
				.getFirstVertexOfClass((VertexClass) g.getSchema()
						.getAttributedElementClass(
								new QualifiedName(vertexClassName))) : g
				.getFirstVertex();
		return (v == null) ? 0 : v.getId();
	}

	public int getNextVertex(int graphId, int vertexId, String vertexClassName) {
		Graph g = graphs.get(graphId);
		Vertex v = (vertexClassName != null) ? g.getVertex(vertexId)
				.getNextVertexOfClass(
						((VertexClass) g.getSchema().getAttributedElementClass(
								new QualifiedName(vertexClassName)))) : g
				.getVertex(vertexId).getNextVertex();
		return (v == null) ? 0 : v.getId();
	}

	public int getFirstEdgeInGraph(int graphId, String edgeClassName) {
		Graph g = graphs.get(graphId);
		Edge e = (edgeClassName != null) ? g
				.getFirstEdgeOfClassInGraph((EdgeClass) g.getSchema()
						.getAttributedElementClass(
								new QualifiedName(edgeClassName))) : g
				.getFirstEdgeInGraph();
		return (e == null) ? 0 : e.getId();
	}

	public int getNextEdgeInGraph(int graphId, int edgeId, String edgeClassName) {
		Graph g = graphs.get(graphId);
		Edge e = (edgeClassName != null) ? g.getEdge(edgeId)
				.getNextEdgeOfClassInGraph(
						((EdgeClass) g.getSchema().getAttributedElementClass(
								new QualifiedName(edgeClassName)))) : g
				.getEdge(edgeId).getNextEdgeInGraph();
		return (e == null) ? 0 : e.getId();
	}

	public int getFirstEdge(int graphId, int vertexId, String edgeClassName) {
		Graph g = graphs.get(graphId);
		Edge e = (edgeClassName != null) ? g.getVertex(vertexId)
				.getFirstEdgeOfClass(
						(EdgeClass) g.getSchema().getAttributedElementClass(
								new QualifiedName(edgeClassName))) : g
				.getVertex(vertexId).getFirstEdge();
		return (e == null) ? 0 : e.getId();
	}

	public int getNextEdge(int graphId, int edgeId, String edgeClassName) {
		Graph g = graphs.get(graphId);
		Edge e = (edgeClassName != null) ? g.getEdge(edgeId)
				.getNextEdgeOfClass(
						((EdgeClass) g.getSchema().getAttributedElementClass(
								new QualifiedName(edgeClassName)))) : g
				.getEdge(edgeId).getNextEdge();
		return (e == null) ? 0 : e.getId();
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------

	private void setEnumAttribute(AttributedElement e, String attributeName,
			Object value) {
		try {
			AttributedElementClass aec = e.getAttributedElementClass();
			Attribute attr = aec.getAttribute(attributeName);
			if (attr == null) {
				throw new GraphException("Attribute " + attributeName
						+ " not defined in class " + aec.getQualifiedName());
			}
			Domain domain = attr.getDomain();
			if (!(domain instanceof EnumDomain)) {
				throw new GraphException("Domain of attribute " + attributeName
						+ " is no EnumDomain.");
			}
			Class<?> attrType = Class.forName(domain.getSchema()
					.getPackageName()
					+ "." + domain.getQualifiedName());
			Object enumValue = attrType.getMethod("fromString",
					new Class[] { String.class }).invoke(null, value);
			if (enumValue == null) {
				throw new GraphException("Enum value " + value
						+ " not defined in domain " + domain.getQualifiedName());
			}
			setAttribute(e, attributeName, enumValue);
		} catch (ClassNotFoundException ex) {
			throw new GraphException(ex);
		} catch (IllegalArgumentException ex) {
			throw new GraphException(ex);
		} catch (SecurityException ex) {
			throw new GraphException(ex);
		} catch (IllegalAccessException ex) {
			throw new GraphException(ex);
		} catch (InvocationTargetException ex) {
			throw new GraphException(ex);
		} catch (NoSuchMethodException ex) {
			throw new GraphException(ex);
		}
	}

	private void setAttribute(AttributedElement e, String attributeName,
			Object value) {
		try {
			e.setAttribute(attributeName, value);
		} catch (NoSuchFieldException ex) {
			throw new GraphException(ex);
		}
	}

}
