package de.uni_koblenz.jgralab.algolib.algorithms.search;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.algorithms.search.visitors.SearchVisitorComposition;
import de.uni_koblenz.jgralab.algolib.functions.BooleanFunction;
import de.uni_koblenz.jgralab.algolib.problems.TraversalFromVertexSolver;
import de.uni_koblenz.jgralab.algolib.visitors.Visitor;

/**
 * This class is the implementation of the breadth first search. This
 * implementation uses the array that computes the vertex order as queue buffer.
 * 
 * @author strauss@uni-koblenz.de
 * 
 */
public class BreadthFirstSearch extends SearchAlgorithm implements
		TraversalFromVertexSolver {

	private SearchVisitorComposition visitors = new SearchVisitorComposition();

	public BreadthFirstSearch(Graph graph,
			BooleanFunction<GraphElement> subgraph,
			BooleanFunction<Edge> navigable) {
		super(graph, subgraph, navigable);
	}

	public BreadthFirstSearch(Graph graph) {
		super(graph);
	}

	private int firstV;

	@Override
	public BreadthFirstSearch withLevel() {
		return (BreadthFirstSearch) super.withLevel();
	}

	@Override
	public BreadthFirstSearch withNumber() {
		return (BreadthFirstSearch) super.withNumber();
	}

	@Override
	public BreadthFirstSearch withParent() {
		return (BreadthFirstSearch) super.withParent();
	}

	@Override
	public BreadthFirstSearch withoutLevel() {
		return (BreadthFirstSearch) super.withoutLevel();
	}

	@Override
	public BreadthFirstSearch withoutNumber() {
		return (BreadthFirstSearch) super.withoutNumber();
	}

	@Override
	public BreadthFirstSearch withoutParent() {
		return (BreadthFirstSearch) super.withoutParent();
	}

	@Override
	public void reset() {
		super.reset();
		firstV = 1;
		visitors.reset();
	}

	@Override
	public void resetParameters() {
		super.resetParameters();
		visitors = new SearchVisitorComposition();
	}

	@Override
	public void addVisitor(Visitor visitor) {
		checkStateForSettingParameters();
		visitor.setAlgorithm(this);
		visitors.addVisitor(visitor);
	}

	@Override
	public void removeVisitor(Visitor visitor) {
		checkStateForSettingParameters();
		visitors.removeVisitor(visitor);
	}

	@Override
	public BreadthFirstSearch execute(Vertex root) {
		if (subgraph != null && !subgraph.get(root)
				|| visitedVertices.get(root)) {
			return this;
		}
		startRunning();
		vertexOrder[num] = root;

		if (level != null) {
			level.set(root, 0);
		}
		visitors.visitRoot(root);

		if (number != null) {
			number.set(root, num);
		}
		visitors.visitVertex(root);

		visitedVertices.set(root, true);
		num++;
		// main loop
		while (firstV < num && vertexOrder[firstV] != null) {
			Vertex currentVertex = vertexOrder[firstV++]; // pop
			for (Edge currentEdge : currentVertex.incidences(searchDirection)) {
				if (subgraph == null || (subgraph.get(currentEdge))
						&& (navigable == null || navigable.get(currentEdge))
						&& !visitedEdges.get(currentEdge)) {
					Vertex nextVertex = currentEdge.getThat();
					// TODO is this check necessary?
					if (subgraph == null || subgraph.get(nextVertex)) {
						edgeOrder[eNum] = currentEdge;
						visitors.visitEdge(currentEdge);
						visitedEdges.set(currentEdge, true);
						eNum++;

						if (visitedVertices.get(nextVertex)) {
							visitors.visitFrond(currentEdge);
						} else {
							visitors.visitTreeEdge(currentEdge);
							vertexOrder[num] = nextVertex;
							if (level != null) {
								level.set(nextVertex,
										level.get(currentVertex) + 1);
							}
							if (parent != null) {
								parent.set(currentEdge.getThat(), currentEdge);
							}
							if (number != null) {
								number.set(nextVertex, num);
							}
							visitors.visitVertex(nextVertex);
							visitedVertices.set(nextVertex, true);
							num++;
						}
					}
				}
			}
		}
		done();
		return this;
	}

}
