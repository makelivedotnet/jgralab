/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2010 Institute for Software Technology
 *               University of Koblenz-Landau, Germany
 * 
 *               ist@uni-koblenz.de
 * 
 * Please report bugs to http://serres.uni-koblenz.de/bugzilla
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.uni_koblenz.jgralabtest.algolib.nonjunit;

import de.uni_koblenz.jgralab.algolib.algorithms.AlgorithmTerminatedException;
import de.uni_koblenz.jgralab.algolib.algorithms.search.DepthFirstSearch;
import de.uni_koblenz.jgralab.algolib.algorithms.search.RecursiveDepthFirstSearch;
import de.uni_koblenz.jgralab.algolib.algorithms.topological_order.TopologicalOrderWithDFS;
import de.uni_koblenz.jgralab.algolib.algorithms.topological_order.KahnKnuthAlgorithm;
import de.uni_koblenz.jgralabtest.schemas.algolib.simple.SimpleGraph;
import de.uni_koblenz.jgralabtest.schemas.algolib.simple.SimpleSchema;
import de.uni_koblenz.jgralabtest.schemas.algolib.simple.SimpleVertex;

public class TryKahnKnuth {
	
	public static SimpleGraph danielsGraph(){
		SimpleGraph graph = SimpleSchema.instance().createSimpleGraph();
		int vertexCount = 7;
		SimpleVertex[] vertices = new SimpleVertex[vertexCount];
		for (int i = 1; i < vertexCount; i++) {
			vertices[i] = graph.createSimpleVertex();
		}
		graph.createSimpleEdge(vertices[5], vertices[2]);
		graph.createSimpleEdge(vertices[2], vertices[3]);
		graph.createSimpleEdge(vertices[2], vertices[4]);
		graph.createSimpleEdge(vertices[4], vertices[1]);
		graph.createSimpleEdge(vertices[5], vertices[1]);
		graph.createSimpleEdge(vertices[6], vertices[3]);
		return graph;
	}
	
	public static SimpleGraph myGraph(){
		SimpleGraph graph = SimpleSchema.instance().createSimpleGraph();
		int vertexCount = 9;
		SimpleVertex[] vertices = new SimpleVertex[vertexCount];
		for (int i = 1; i < vertexCount; i++) {
			vertices[i] = graph.createSimpleVertex();
		}
		graph.createSimpleEdge(vertices[5], vertices[1]);
		graph.createSimpleEdge(vertices[3], vertices[1]);
		graph.createSimpleEdge(vertices[3], vertices[2]);
		graph.createSimpleEdge(vertices[4], vertices[2]);
		graph.createSimpleEdge(vertices[5], vertices[3]);
		graph.createSimpleEdge(vertices[4], vertices[3]);
		graph.createSimpleEdge(vertices[7], vertices[4]);
		graph.createSimpleEdge(vertices[6], vertices[5]);
		graph.createSimpleEdge(vertices[7], vertices[6]);
		graph.createSimpleEdge(vertices[1], vertices[8]);
		graph.createSimpleEdge(vertices[2], vertices[8]);
		return graph;
	}
	public static void main(String[] args) throws AlgorithmTerminatedException {
		
		SimpleGraph graph = danielsGraph();
		
		// graph.createSimpleEdge(vertices[7], vertices[3]);

		KahnKnuthAlgorithm solver = new KahnKnuthAlgorithm(graph);
		DepthFirstSearch dfs = new RecursiveDepthFirstSearch(graph);
		TopologicalOrderWithDFS solver2 = new TopologicalOrderWithDFS(graph, dfs);
		dfs.addVisitor(new DebugSearchVisitor());
		System.out.println("Kahn Knuth:");

		solver.execute();

		System.out.println(solver.isAcyclic());
		System.out.println(solver.getTopologicalOrder());

		System.out.println();
		System.out.println("DFS:");

		solver2.execute();

		System.out.println(solver2.isAcyclic());
		System.out.println(solver2.getTopologicalOrder());

	}
}