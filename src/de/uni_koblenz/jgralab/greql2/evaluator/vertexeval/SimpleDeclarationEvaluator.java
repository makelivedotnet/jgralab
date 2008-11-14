/*
 * JGraLab - The Java graph laboratory
 * (c) 2006-2008 Institute for Software Technology
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

package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.HashSet;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclaration;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.GraphSize;
import de.uni_koblenz.jgralab.greql2.evaluator.costmodel.VertexCosts;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.JValueInvalidTypeException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueList;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueSet;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsTypeExprOf;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Evaluates a simple declaration. Creates a VariableDeclaration-object, that
 * provides methods to iterate over all possible values.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class SimpleDeclarationEvaluator extends VertexEvaluator {

	private SimpleDeclaration vertex;

	/**
	 * returns the vertex this VertexEvaluator evaluates
	 */
	@Override
	public Vertex getVertex() {
		return vertex;
	}

	/**
	 * @param eval
	 *            the SimpleDeclarationEvaluator this VertexEvaluator belongs to
	 * @param vertex
	 *            the vertex which gets evaluated by this VertexEvaluator
	 */
	public SimpleDeclarationEvaluator(SimpleDeclaration vertex,
			GreqlEvaluator eval) {
		super(eval);
		this.vertex = vertex;
	}

	/**
	 * returns a JValueList of VariableDeclaration objects
	 */
	@Override
	public JValue evaluate() throws EvaluateException {
		IsTypeExprOf inc = vertex.getFirstIsTypeExprOf(EdgeDirection.IN);
		Expression typeExpression = (Expression) inc.getAlpha();
		VertexEvaluator exprEval = greqlEvaluator
				.getVertexEvaluatorGraphMarker().getMark(typeExpression);
		if (exprEval instanceof VertexSubgraphExpressionEvaluator) {
			inc = inc.getNextIsTypeExprOf(EdgeDirection.IN);
			typeExpression = (Expression) inc.getAlpha();
			exprEval = greqlEvaluator.getVertexEvaluatorGraphMarker().getMark(
					typeExpression);
		}
		JValue tempAttribute = exprEval.getResult(subgraph);
		JValueSet declarationSet = null;
		if (tempAttribute.isCollection()) {
			try {
				JValueCollection col = tempAttribute.toCollection();
				declarationSet = col.toJValueSet();
				if (col.size() > declarationSet.size())
					throw new EvaluateException(
							"A collection that doesn't fulfill the set property is used as variable range definition");
			} catch (JValueInvalidTypeException exception) {
				throw new EvaluateException(
						"Error evaluating a SimpleDeclaration : "
								+ exception.toString());
			}
		} else {
			declarationSet = new JValueSet();
			declarationSet.add(tempAttribute);
		}
		if (declarationSet != null) {
			JValueList varDeclList = new JValueList();
			IsDeclaredVarOf varInc = vertex
					.getFirstIsDeclaredVarOf(EdgeDirection.IN);
			while (varInc != null) {
				VariableDeclaration varDecl = new VariableDeclaration(
						(Variable) varInc.getAlpha(), declarationSet, vertex,
						greqlEvaluator);
				varDeclList.add(new JValue(varDecl));
				varInc = varInc.getNextIsDeclaredVarOf(EdgeDirection.IN);
			}
			return varDeclList;
		}
		return null;
	}

	@Override
	public VertexCosts calculateSubtreeEvaluationCosts(GraphSize graphSize) {
		return this.greqlEvaluator.getCostModel()
				.calculateCostsSimpleDeclaration(this, graphSize);
	}

	@Override
	public void calculateNeededAndDefinedVariables() {
		neededVariables = new HashSet<Variable>();
		definedVariables = new HashSet<Variable>();
		IsDeclaredVarOf varInc = vertex
				.getFirstIsDeclaredVarOf(EdgeDirection.IN);
		while (varInc != null) {
			definedVariables.add((Variable) varInc.getAlpha());
			varInc = varInc.getNextIsDeclaredVarOf(EdgeDirection.IN);
		}
		IsTypeExprOf typeInc = vertex.getFirstIsTypeExprOf(EdgeDirection.IN);
		if (typeInc != null) {
			VertexEvaluator veval = greqlEvaluator
					.getVertexEvaluatorGraphMarker()
					.getMark(typeInc.getAlpha());
			if (veval != null) {
				neededVariables.addAll(veval.getNeededVariables());
			}
		}
	}

	@Override
	public long calculateEstimatedCardinality(GraphSize graphSize) {
		return greqlEvaluator.getCostModel()
				.calculateCardinalitySimpleDeclaration(this, graphSize);
	}

}
