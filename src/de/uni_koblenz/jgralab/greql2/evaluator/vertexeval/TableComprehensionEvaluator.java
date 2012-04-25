/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * For bug reports, documentation and further information, visit
 *
 *                         https://github.com/jgralab/jgralab
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

package de.uni_koblenz.jgralab.greql2.evaluator.vertexeval;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.pcollections.PVector;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.greql2.evaluator.InternalGreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.VariableDeclarationLayer;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.IsTableHeaderOf;
import de.uni_koblenz.jgralab.greql2.schema.TableComprehension;
import de.uni_koblenz.jgralab.greql2.types.Table;
import de.uni_koblenz.jgralab.greql2.types.Tuple;

/**
 * Evaluates a TableComprehensionvertex in the GReQL-2 Syntaxgraph. A
 * TableComprehension vertex is constructed using the notation reportTable
 * columHeader, rowHeader, cellContent
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class TableComprehensionEvaluator extends
		VertexEvaluator<TableComprehension> {

	private VariableDeclarationLayer declarationLayer;

	private VertexEvaluator<? extends Expression> columnHeaderEval = null;

	private VertexEvaluator<? extends Expression> rowHeaderEval = null;

	private VertexEvaluator<? extends Expression> resultDefEval = null;

	private boolean initialized = false;

	private void initialize(InternalGreqlEvaluator evaluator) {
		Declaration d = (Declaration) vertex.getFirstIsCompDeclOfIncidence(
				EdgeDirection.IN).getAlpha();
		DeclarationEvaluator declEval = (DeclarationEvaluator) query
				.getVertexEvaluator(d);
		declarationLayer = (VariableDeclarationLayer) declEval
				.getResult(evaluator);

		Expression columnHeader = (Expression) vertex
				.getFirstIsColumnHeaderExprOfIncidence(EdgeDirection.IN)
				.getAlpha();
		columnHeaderEval = query.getVertexEvaluator(columnHeader);
		Expression rowHeader = (Expression) vertex
				.getFirstIsRowHeaderExprOfIncidence(EdgeDirection.IN)
				.getAlpha();
		rowHeaderEval = query.getVertexEvaluator(rowHeader);
		Expression resultDef = (Expression) vertex
				.getFirstIsCompResultDefOfIncidence(EdgeDirection.IN)
				.getAlpha();
		resultDefEval = query.getVertexEvaluator(resultDef);
		initialized = true;
	}

	/**
	 * Creates a new TableComprehensionEvaluator for the given vertex
	 * 
	 * @param eval
	 *            the GreqlEvaluator instance this VertexEvaluator belong to
	 * @param vertex
	 *            the vertex this VertexEvaluator evaluates
	 */
	public TableComprehensionEvaluator(TableComprehension vertex,
			QueryImpl query) {
		super(vertex, query);
	}

	@Override
	public Object evaluate(InternalGreqlEvaluator evaluator) {
		if (!initialized) {
			initialize(evaluator);
		}
		TreeMap<Object, HashMap<Object, Object>> tableMap = new TreeMap<Object, HashMap<Object, Object>>();
		Set<Object> completeColumnHeaderTuple = new HashSet<Object>();
		TreeSet<Object> rowHeaderSet = new TreeSet<Object>();

		declarationLayer.reset();
		while (declarationLayer.iterate(evaluator)) {
			Object columnHeaderEntry = columnHeaderEval.getResult(evaluator);
			completeColumnHeaderTuple.add(columnHeaderEntry);
			Object rowHeaderEntry = rowHeaderEval.getResult(evaluator);
			Object localResult = resultDefEval.getResult(evaluator);
			HashMap<Object, Object> row = tableMap.get(rowHeaderEntry);
			if (row == null) {
				row = new HashMap<Object, Object>();
				tableMap.put(rowHeaderEntry, row);
				rowHeaderSet.add(rowHeaderEntry);
				// GreqlEvaluator.println("Adding row");
			}
			row.put(columnHeaderEntry, localResult);
		}

		Table<Object> resultTable = Table.empty();
		PVector<String> headerTuple = resultTable.getTitles();
		TreeSet<Object> completeColumnHeaderTreeSet = new TreeSet<Object>();
		for (Object jValueImpl : completeColumnHeaderTuple) {
			completeColumnHeaderTreeSet.add(jValueImpl);
		}
		Iterator<Object> colIter = completeColumnHeaderTreeSet.iterator();
		IsTableHeaderOf tHeader = vertex
				.getFirstIsTableHeaderOfIncidence(EdgeDirection.IN);
		if (tHeader != null) {
			VertexEvaluator<? extends Expression> theval = query
					.getVertexEvaluator((Expression) tHeader.getAlpha());
			headerTuple = headerTuple
					.plus((String) theval.getResult(evaluator));
		} else {
			headerTuple.plus(""); // dummy entry in the upper
			// left
			// corner
		}
		while (colIter.hasNext()) {
			headerTuple = headerTuple.plus(colIter.next().toString());
		}
		resultTable = resultTable.withTitles(headerTuple);
		Iterator<Entry<Object, HashMap<Object, Object>>> rowIter = tableMap
				.entrySet().iterator();
		while (rowIter.hasNext()) {
			Entry<Object, HashMap<Object, Object>> currentEntry = rowIter
					.next();
			Object currentRowHeader = currentEntry.getKey();
			HashMap<Object, Object> currentRow = currentEntry.getValue();
			colIter = completeColumnHeaderTreeSet.iterator();
			Tuple rowTuple = Tuple.empty();
			rowTuple = rowTuple.plus(currentRowHeader);
			while (colIter.hasNext()) {
				Object cellEntry = currentRow.get(colIter.next());
				rowTuple = rowTuple.plus(cellEntry);
			}
			resultTable = resultTable.plus(rowTuple);
		}
		return resultTable;
	}

	// @Override
	// public VertexCosts calculateSubtreeEvaluationCosts() {
	// return greqlEvaluator.getCostModel().calculateCostsTableComprehension(
	// this);
	// }
	//
	// @Override
	// public long calculateEstimatedCardinality() {
	// return greqlEvaluator.getCostModel()
	// .calculateCardinalityTableComprehension(this);
	// }

}
