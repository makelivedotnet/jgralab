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

package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;
import de.uni_koblenz.jgralab.graphmarker.GraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.DFA;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.State;
import de.uni_koblenz.jgralab.greql2.evaluator.fa.Transition;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.funlib.pathsearch.PathSystemMarkerEntry;
import de.uni_koblenz.jgralab.greql2.funlib.pathsearch.PathSystemMarkerList;
import de.uni_koblenz.jgralab.greql2.funlib.pathsearch.PathSystemQueueEntry;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValuePathSystem;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

/**
 * Returns a pathsystem, based on the current graph and the given dfa, whose
 * root is the given vertex.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>PATHSYSTEM pathSystem(v:VERTEX, dfa:AUTOMATON)</code></dd>
 * <dd>&nbsp;</dd>
 * <dd>This function can be used with the (<code>:-)</code>)-Operator:
 * <code>v :-) rpe</code></dd>
 * <dd><code>rpe</code> is a regular path expression.</dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>v</code> - root of the returned pathsystem</dd>
 * <dd><code>dfa</code> - a dfa that accepts regular path expressions</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>a pathsystem, based on the current graph and the given dfa, whose root is
 * the given vertex</dd>
 * <dd><code>Null</code> if one of the given parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class PathSystem extends Greql2Function {

	{
		JValueType[][] x = { { JValueType.VERTEX, JValueType.AUTOMATON,
				JValueType.PATHSYSTEM } };
		signatures = x;

		description = "Returns a pathsystem with root vertex, which is structured according to path description.";

		Category[] c = { Category.PATHS_AND_PATHSYSTEMS_AND_SLICES };
		categories = c;
	}

	/**
	 * for each state in the finite automaton a seperate GraphMarker is used
	 */
	private List<GraphMarker<PathSystemMarkerList>> marker;

	/**
	 * The graph the search is performed on
	 */
	private Graph graph;

	/**
	 * Holds a set of states for each AttributedElement
	 */
	private GraphMarker<Set<State>> stateMarker;

	/**
	 * marks the given vertex with the given PathSystemMarker
	 * 
	 * @return true if the vertex was marked successfull, false if it is already
	 *         marked with this state
	 */
	protected boolean markVertex(Vertex v, State s, Vertex parentVertex,
			Edge e, State ps, int d) {
		PathSystemMarkerEntry m = new PathSystemMarkerEntry(parentVertex, e, s,
				ps, d);
		GraphMarker<PathSystemMarkerList> currentMarker = getGraphMarkerForState(s.number);
		PathSystemMarkerList list = currentMarker.getMark(v);
		if (list == null) {
			list = new PathSystemMarkerList(s, v);
			currentMarker.mark(v, list);
		}
		list.put(parentVertex, m);
		return true;
	}

	/**
	 * @param stateNumber
	 * @return the GraphMarker for the given state number
	 */
	private final GraphMarker<PathSystemMarkerList> getGraphMarkerForState(
			int stateNumber) {
		GraphMarker<PathSystemMarkerList> currentMarker = marker
				.get(stateNumber);
		if (currentMarker == null) {
			currentMarker = new GraphMarker<PathSystemMarkerList>(graph);
			marker.set(stateNumber, currentMarker);
		}
		return currentMarker;
	}

	/**
	 * Checks if the given vertex is marked with the given state
	 * 
	 * @return true if the vertex is marked, false otherwise
	 */
	protected boolean isMarked(Vertex v, State s) {
		GraphMarker<PathSystemMarkerList> currentMarker = marker.get(s.number);
		if (currentMarker == null) {
			return false;
		}
		PathSystemMarkerList list = currentMarker.getMark(v);
		return list != null;
	}

	/**
	 * Marks all vertices that are part of the PathSystem described by the given
	 * rootVertex and the regular path expression which is acceptes by the given
	 * dfa
	 * 
	 * @param startVertex
	 *            the rootVertex of the PathSystem
	 * @param dfa
	 *            the DFA which accepts the regular path expression that
	 *            describes the pathsystem
	 * @param subgraph
	 *            the subgraph all parts of the pathsystem belong to
	 * @return the list of leaves in the pathsystem, that is the set of states
	 *         which are marked with a final state of the dfa
	 * @throws EvaluateException
	 *             if something went wrong, several EvaluateException can be
	 *             thrown
	 */
	private List<Vertex> markVerticesOfPathSystem(Vertex startVertex, DFA dfa,
			AbstractGraphMarker<AttributedElement> subgraph)
			throws EvaluateException {
		// GreqlEvaluator.errprintln("Start marking vertices of path system");
		ArrayList<Vertex> finalVertices = new ArrayList<Vertex>();
		Queue<PathSystemQueueEntry> queue = new LinkedList<PathSystemQueueEntry>();
		PathSystemQueueEntry currentEntry = new PathSystemQueueEntry(
				startVertex, dfa.initialState, null, null, 0);
		markVertex(startVertex, dfa.initialState, null /* no parent state */,
				null /* no parent vertex */, null /* no parent state */, 0 /*
																		 * distance
																		 * to
																		 * root
																		 * is
																		 * null
																		 */);

		int count = 0, countWTrans = 0;
		while (currentEntry != null) {
			if (currentEntry.state.isFinal) {
				finalVertices.add(currentEntry.vertex);
			}
			Edge inc = currentEntry.vertex.getFirstEdge();
			while (inc != null) {
				count++;

				Iterator<Transition> transitionIter = currentEntry.state.outTransitions
						.iterator();
				while (transitionIter.hasNext()) {
					countWTrans++;
					Transition currentTransition = transitionIter.next();
					Vertex nextVertex = currentTransition.getNextVertex(
							currentEntry.vertex, inc);
					if (!isMarked(nextVertex, currentTransition.endState)) {
						if (currentTransition.accepts(currentEntry.vertex, inc,
								subgraph)) {
							Edge traversedEdge = inc;
							if (nextVertex == currentEntry.vertex) {
								traversedEdge = null;
							}
							markVertex(nextVertex, currentTransition.endState,
									currentEntry.vertex, traversedEdge,
									currentEntry.state,
									currentEntry.distanceToRoot + 1);
							PathSystemQueueEntry nextEntry = new PathSystemQueueEntry(
									nextVertex, currentTransition.endState,
									traversedEdge, currentEntry.state,
									currentEntry.distanceToRoot + 1);
							queue.add(nextEntry);
						}
					}
				}
				inc = inc.getNextEdge();
			}
			currentEntry = queue.poll();
		}

		return finalVertices;
	}

	/**
	 * creates the pathsystem
	 */
	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {
		DFA dfa = null;
		switch (checkArguments(arguments)) {
		case 0:
			dfa = arguments[1].toAutomaton().getDFA();
			break;
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}

		Vertex startVertex = arguments[0].toVertex();
		this.graph = graph;

		marker = new ArrayList<GraphMarker<PathSystemMarkerList>>(dfa.stateList
				.size());
		for (int i = 0; i < dfa.stateList.size(); i++) {
			marker.add(new GraphMarker<PathSystemMarkerList>(graph));
		}
		List<Vertex> leaves = markVerticesOfPathSystem(startVertex, dfa,
				subgraph);
		JValuePathSystem resultPathSystem = createPathSystemFromMarkings(
				startVertex, leaves);
		return resultPathSystem;
	}

	/**
	 * Creates a JValuePathSystem-object which contains all path which start at
	 * the given root vertex andend with the given leaves
	 * 
	 * @param leaves
	 * @return
	 */
	private JValuePathSystem createPathSystemFromMarkings(Vertex rootVertex,
			List<Vertex> leaves) {
		JValuePathSystem pathSystem = new JValuePathSystem(rootVertex
				.getGraph());
		PathSystemMarkerList rootMarkerList = marker.get(0).getMark(rootVertex);
		PathSystemMarkerEntry rootMarker = rootMarkerList
				.getPathSystemMarkerEntryWithParentVertex(null);
		pathSystem.setRootVertex(rootVertex, rootMarker.state.number,
				rootMarker.state.isFinal);
		stateMarker = new GraphMarker<Set<State>>(rootVertex.getGraph());
		Iterator<Vertex> iter = leaves.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Vertex leaf = iter.next();
			for (GraphMarker<PathSystemMarkerList> currentGraphMarker : marker) {
				Object tempAttribute = currentGraphMarker.getMark(leaf);
				if (tempAttribute != null) {
					for (PathSystemMarkerEntry currentMarker : ((PathSystemMarkerList) tempAttribute)
							.values()) {
						if (!currentMarker.state.isFinal || // if state of
								// current PathSystemMarkerEntry is final or
								isVertexMarkedWithState(leaf,
										currentMarker.state)) { // (leaf, state)
							// has already been processed
							continue;
						}
						Vertex currentVertex = leaf;
						while (currentVertex != null
								&& !isVertexMarkedWithState(currentVertex,
										currentMarker.state)) {
							int parentStateNumber = 0;
							if (currentMarker.parentState != null) {
								parentStateNumber = currentMarker.parentState.number;
							}
							count++;
							pathSystem.addVertex(currentVertex,
									currentMarker.state.number,
									currentMarker.edgeToParentVertex,
									currentMarker.parentVertex,
									parentStateNumber,
									currentMarker.distanceToRoot,
									currentMarker.state.isFinal);
							markVertexWithState(currentVertex,
									currentMarker.state);
							currentVertex = currentMarker.parentVertex;
							currentMarker = getMarkerWithState(currentVertex,
									currentMarker.parentState);
						}
					}
				}
			}
		}
		// System.out.println("PathSystem - createPathSystem: " + count);

		return pathSystem;
	}

	/**
	 * Adds the given state to the set of states maintained for the given
	 * vertex.
	 * 
	 * @param v
	 *            the vertex to be marked
	 * @param s
	 *            the state which shall be added to {@code v}'s state set
	 */
	private void markVertexWithState(Vertex v, State s) {
		if (stateMarker.getMark(v) == null) {
			stateMarker.mark(v, new HashSet<State>());
		}
		stateMarker.getMark(v).add(s);
	}

	/**
	 * Checks if the given vertex' state set contains the given state.
	 * 
	 * @param v
	 *            the vertex to be checked
	 * @param s
	 *            the state to be checked for
	 * @return true, if {@code v}'s set of states contains {@code s}, false else
	 */
	private boolean isVertexMarkedWithState(Vertex v, State s) {
		if (stateMarker.getMark(v) == null) {
			return false;
		}
		return stateMarker.getMark(v).contains(s);
	}

	/**
	 * Returns the {@code PathSystemMarkerEntry} for a given vertex and state.
	 * 
	 * @param v
	 *            the vertex for which to return the {@code
	 *            PathSystemMarkerEntry}
	 * @param s
	 *            the state for which to return the {@code
	 *            PathSystemMarkerEntry}
	 * @return the {@code PathSystemMarkerEntry} for {@code v} and {@code s}
	 */
	private PathSystemMarkerEntry getMarkerWithState(Vertex v, State s) {
		if (v == null) {
			return null;
		}
		GraphMarker<PathSystemMarkerList> currentMarker = marker.get(s.number);
		PathSystemMarkerList list = currentMarker.getMark(v);
		Iterator<PathSystemMarkerEntry> iter = list.values().iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 1000;
	}

	@Override
	public double getSelectivity() {
		return 0.001f;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

}
