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

package de.uni_koblenz.jgralab.utilities.tg2dot.greql2.funlib;

import java.util.ArrayList;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.funlib.Greql2Function;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueImpl;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueType;

/**
 * Returns the omega incidence number of the given edge.
 * 
 * <dl>
 * <dt><b>GReQL-signatures</b></dt>
 * <dd><code>INT omegaIncidenceNumber(ae:EDGE)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>ae</code> - edge or edge class to return the alpha incidence number
 * for</dd>
 * <dt><b>Returns:</b></dt>
 * <dd>the omega incidence number of the given edge or edge class as String</dd>
 * <dd><code>Null</code> if one of the parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class OmegaIncidenceNumber extends Greql2Function {
	{
		JValueType[][] x = { { JValueType.EDGE, JValueType.STRING } };
		signatures = x;

		description = "Returns the omega incidence number of the given edge.";

		Category[] c = { Category.GRAPH };
		categories = c;
	}

	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {

		switch (checkArguments(arguments)) {
		case 0:
			Edge edge = arguments[0].toEdge();
			return new JValueImpl(getOmegaIncidenceNumber(edge));
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
	}

	/**
	 * Returns the omega incidence number for the given Edge.
	 * 
	 * @param edge
	 *            Edge of which the incidence number should be found out.
	 * @return Incidence number.
	 */
	private int getOmegaIncidenceNumber(Edge edge) {
		int num = 0;
		edge = edge.getReversedEdge();
		for (Edge incidence : edge.getOmega().incidences()) {
			if (incidence == edge) {
				return num;
			}
			num++;
		}
		return -1;
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 2;
	}

	@Override
	public double getSelectivity() {
		return 1;
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

}