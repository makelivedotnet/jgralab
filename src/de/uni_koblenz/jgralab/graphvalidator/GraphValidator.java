/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2009 Institute for Software Technology
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
package de.uni_koblenz.jgralab.graphvalidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphvalidator.ConstraintInvalidation.ConstraintType;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;

/**
 * @author Tassilo Horn <horn@uni-koblenz.de>
 *
 */
public class GraphValidator {

	private Graph graph;

	public GraphValidator(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Validate the graph
	 *
	 * @return a set of {@link ConstraintInvalidation} objects, one for each
	 *         invalidation, sorted by the {@link ConstraintType}
	 */
	public SortedSet<ConstraintInvalidation> validate() {
		SortedSet<ConstraintInvalidation> brokenConstraints = new TreeSet<ConstraintInvalidation>();

		// Check if all multiplicities are correct
		for (EdgeClass ec : graph.getSchema()
				.getEdgeClassesInTopologicalOrder()) {
			if (ec.isInternal()) {
				continue;
			}

			int fromMin = ec.getFromMin();
			int fromMax = ec.getFromMax();
			int toMin = ec.getToMin();
			int toMax = ec.getToMax();

			for (Vertex v : graph.vertices(ec.getFrom())) {
				int degree = v.getDegree(ec);
				if (degree < toMin || degree > toMax) {
					brokenConstraints.add(new ConstraintInvalidation(
							ConstraintType.MULTIPLICITY, v + " has " + degree
									+ " outgoing " + ec.getQualifiedName()
									+ " edges, but only " + toMin + " to "
									+ toMax + " are allowed."));
				}
			}
			for (Vertex v : graph.vertices(ec.getTo())) {
				int degree = v.getDegree(ec);
				if (degree < fromMin || degree > fromMax) {
					brokenConstraints.add(new ConstraintInvalidation(
							ConstraintType.MULTIPLICITY, v + " has " + degree
									+ " incoming " + ec.getQualifiedName()
									+ " edges, but only " + fromMin + " to "
									+ fromMax + " are allowed."));
				}
			}
		}

		// check if all greql constraints are met
		List<AttributedElementClass> aecs = new ArrayList<AttributedElementClass>();
		aecs.addAll(graph.getSchema().getGraphClassesInTopologicalOrder());
		aecs.addAll(graph.getSchema().getVertexClassesInTopologicalOrder());
		aecs.addAll(graph.getSchema().getEdgeClassesInTopologicalOrder());
		for (AttributedElementClass vc : aecs) {
			for (String greql2Exp : vc.getConstraints()) {
				GreqlEvaluator eval = new GreqlEvaluator(greql2Exp, graph, null);
				try {
					eval.startEvaluation();
					JValue result = eval.getEvaluationResult();
					handleGreqlResult(result, greql2Exp, brokenConstraints);
				} catch (EvaluateException e) {
					brokenConstraints.add(new ConstraintInvalidation(
							ConstraintType.INVALID_GREQL_EXPRESSION,
							"The GReQL query \"" + greql2Exp
									+ "\" is broken.\n" + e.getMessage()));
				}
			}
		}
		return brokenConstraints;
	}

	/**
	 * Do just like {@link GraphValidator#validate()}, but generate a HTML
	 * report saved to <code>fileName</code>, too.
	 *
	 * @param fileName
	 *            the name of the HTML report file
	 * @return a set of {@link ConstraintInvalidation} objects, one for each
	 *         invalidation
	 * @throws IOException
	 *             if the given file cannot be written
	 */
	public SortedSet<ConstraintInvalidation> createValidationReport(
			String fileName) throws IOException {
		SortedSet<ConstraintInvalidation> brokenConstraints = validate();

		BufferedWriter bw = new BufferedWriter(new FileWriter(
				new File(fileName)));
		// The header
		bw.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"
				+ "\"http://www.w3.org/TR/html4/strict.dtd\">\n" + "<html>");
		bw.append("<head><title>");
		bw.append("Validation Report for the "
				+ graph.getM1Class().getSimpleName() + " with id "
				+ graph.getId() + ".");
		bw.append("</title></head>");

		// The body
		bw.append("<body>");

		if (brokenConstraints.size() == 0) {
			bw.append("<p><b>The graph is perfectly valid!</b></p>");
		} else {
			bw.append("<p><b>The " + graph.getM1Class().getSimpleName()
					+ " invalidates " + brokenConstraints.size()
					+ " constraints.</b></p>");
			// Here goes the table
			bw.append("<table border=\"1\">");
			bw.append("<tr>");
			bw.append("<th>#</th>");
			bw.append("<th>Constraint Type</th>");
			bw.append("<th>Message</th>");
			bw.append("</tr>");
			int row = 1;
			for (ConstraintInvalidation ci : brokenConstraints) {
				bw.append("<tr>");
				bw.append("<td align=\"right\">" + row + "</td>");
				bw.append("<td>" + ci.getConstraintType() + "</td>");
				bw.append("<td>" + ci.getMessage() + "</td>");
				bw.append("</tr>");
				row++;
			}
			bw.append("</table>");
		}

		bw.append("</body></html>");
		bw.flush();
		bw.close();
		return brokenConstraints;
	}

	private void handleGreqlResult(JValue result, String greqlExp,
			Set<ConstraintInvalidation> brokenConstraints) {
		if (result.isBoolean()) {
			if (!result.toBoolean()) {
				brokenConstraints.add(new ConstraintInvalidation(
						ConstraintType.GREQL, "\"" + greqlExp + "\" returned "
								+ result.toBoolean() + "."));
			}
		} else if (result.isCollection()) {
			JValueCollection c = result.toCollection();
			for (JValue jv : c) {
				brokenConstraints.add(new ConstraintInvalidation(
						ConstraintType.GREQL, jv.toString()));
			}
		} else {
			// TODO: normally we shouldn't get here, so maybe we should handle
			// that situation more appropriate...
			brokenConstraints.add(new ConstraintInvalidation(
					ConstraintType.GREQL, "\"" + greqlExp + "\" returned "
							+ result.toString() + "."));
		}
	}
}
