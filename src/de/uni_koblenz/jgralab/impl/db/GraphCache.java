/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.impl.db;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

import de.uni_koblenz.jgralab.Graph;

/**
 * Global cache for graphs.
 * 
 * @author ultbreit@uni-koblenz.de
 */
public class GraphCache {

	private Hashtable<Graph, Hashtable<Integer, SoftReference<DatabasePersistableVertex>>> verticesOfGraphs;

	private ReferenceQueue<DatabasePersistableVertex> vertexReferenceQueue;

	private Hashtable<Graph, Hashtable<Integer, SoftReference<DatabasePersistableEdge>>> edgesOfGraphs;

	private ReferenceQueue<DatabasePersistableEdge> edgeReferenceQueue;

	private int THRESHOLD = 10000;

	private int accessCounter = 0;

	/**
	 * Creates and initializes a new <code>GraphCache</graph>.
	 */
	public GraphCache() {
		this.verticesOfGraphs = new Hashtable<Graph, Hashtable<Integer, SoftReference<DatabasePersistableVertex>>>();
		this.vertexReferenceQueue = new ReferenceQueue<DatabasePersistableVertex>();
		this.edgesOfGraphs = new Hashtable<Graph, Hashtable<Integer, SoftReference<DatabasePersistableEdge>>>();
		this.edgeReferenceQueue = new ReferenceQueue<DatabasePersistableEdge>();
	}

	private void checkThreshold() {
		this.accessCounter++;
		if (this.accessCounter > THRESHOLD) {
			this.clearReferenceQueues();
			this.accessCounter = 0;
		}
	}

	/**
	 * Adds a vertex to cache.
	 * 
	 * @param vertex
	 *            Vertex to add.
	 */
	public void addVertex(DatabasePersistableVertex vertex) {
		checkThreshold();
		Hashtable<Integer, SoftReference<DatabasePersistableVertex>> vertices;
		if (this.verticesOfGraphs.containsKey(vertex.getGraph())) {
			vertices = this.verticesOfGraphs.get(vertex.getGraph());
		} else {
			vertices = new Hashtable<Integer, SoftReference<DatabasePersistableVertex>>();
			this.verticesOfGraphs.put(vertex.getGraph(), vertices);
		}
		vertices.put(vertex.getId(),
				new SoftReference<DatabasePersistableVertex>(vertex,
						this.vertexReferenceQueue));
	}

	/**
	 * Checks if cache contains vertex with given id of given a given graph.
	 * 
	 * @param graph
	 *            Graph the vertex belongs to.
	 * @param vId
	 *            Id of vertex.
	 * @return true if cache contains vertex with given id of given a given
	 *         graph, otherwise false.
	 */
	public boolean containsVertex(Graph graph, int vId) {
		checkThreshold();
		if (this.verticesOfGraphs.containsKey(graph)) {
			Hashtable<Integer, SoftReference<DatabasePersistableVertex>> vertices = this.verticesOfGraphs
					.get(graph);
			return vertices.containsKey(vId) && vertices.get(vId).get() != null;
		} else {
			return false;
		}
	}

	/**
	 * Gets a vertex with given id of given graph from cache.
	 * 
	 * @param graph
	 *            Graph the vertex belongs to.
	 * @param vId
	 *            Id of vertex.
	 * @return A vertex or null if cache does not contain requested vertex.
	 */
	public DatabasePersistableVertex getVertex(Graph graph, int vId) {
		checkThreshold();
		if (this.verticesOfGraphs.containsKey(graph)) {
			Hashtable<Integer, SoftReference<DatabasePersistableVertex>> vertices = this.verticesOfGraphs
					.get(graph);
			return vertices.get(vId).get();
		} else {
			return null;
		}
	}

	/**
	 * Removes a vertex from cache.
	 * 
	 * @param graph
	 *            Graph the vertex to remove belongs to.
	 * @param vId
	 *            Identifier of vertex to remove from cache.
	 */
	public void removeVertex(DatabasePersistableGraph graph, int vId) {
		checkThreshold();
		Hashtable<Integer, SoftReference<DatabasePersistableVertex>> vertices = this.verticesOfGraphs
				.get(graph);
		vertices.remove(vId);
	}

	/**
	 * Adds an edge to cache.
	 * 
	 * @param edge
	 *            Edge to add.
	 */
	public void addEdge(DatabasePersistableEdge edge) {
		checkThreshold();
		Hashtable<Integer, SoftReference<DatabasePersistableEdge>> edges;
		if (this.edgesOfGraphs.containsKey(edge.getGraph())) {
			edges = this.edgesOfGraphs.get(edge.getGraph());
		} else {
			edges = new Hashtable<Integer, SoftReference<DatabasePersistableEdge>>();
			this.edgesOfGraphs.put(edge.getGraph(), edges);
		}
		edges.put(edge.getId(), new SoftReference<DatabasePersistableEdge>(
				edge, this.edgeReferenceQueue));
	}

	/**
	 * Checks if cache contains edge with given id of given a given graph.
	 * 
	 * @param graph
	 *            Graph the edge belongs to.
	 * @param eId
	 *            Id of edge.
	 * @return true if cache contains edge with given id of given a given graph,
	 *         otherwise false.
	 */
	public boolean containsEdge(Graph graph, int eId) {
		checkThreshold();
		if (this.edgesOfGraphs.containsKey(graph)) {
			Hashtable<Integer, SoftReference<DatabasePersistableEdge>> edges = this.edgesOfGraphs
					.get(graph);
			return edges.containsKey(Math.abs(eId))
					&& edges.get(Math.abs(eId)).get() != null;
		} else {
			return false;
		}
	}

	/**
	 * Gets an edge with given id of given graph from cache.
	 * 
	 * @param graph
	 *            Graph the edge belongs to.
	 * @param eId
	 *            Id of edge.
	 * @return A edge or null if cache does not contain requested edge.
	 */
	public DatabasePersistableEdge getEdge(Graph graph, int eId) {
		checkThreshold();
		if (this.edgesOfGraphs.containsKey(graph)) {
			Hashtable<Integer, SoftReference<DatabasePersistableEdge>> edges = this.edgesOfGraphs
					.get(graph);
			return edges.get(eId).get();
		} else {
			return null;
		}
	}

	/**
	 * Removes an edge from cache.
	 * 
	 * @param graph
	 *            Graph that contains the edge.
	 * @param eId
	 *            Identifier of edge to remove from cache.
	 */
	public void removeEdge(DatabasePersistableGraph graph, int eId) {
		checkThreshold();
		Hashtable<Integer, SoftReference<DatabasePersistableEdge>> edges = this.edgesOfGraphs
				.get(graph);
		edges.remove(eId);
	}

	/**
	 * Clears cache.
	 */
	public void clear() {
		this.edgesOfGraphs.clear();
		this.clearReferenceQueues();
		this.verticesOfGraphs.clear();
	}

	/**
	 * Clears reference queue from dead WeakReferences by polling it.
	 */
	private void clearReferenceQueues() {
		while (this.vertexReferenceQueue.poll() != null) {
			;
		}
		while (this.edgeReferenceQueue.poll() != null) {
			;
		}
	}
}