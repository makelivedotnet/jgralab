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

package de.uni_koblenz.jgralab.greql2.funlib;

import java.util.ArrayList;

import de.uni_koblenz.jgralab.BooleanGraphMarker;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.exception.EvaluateException;
import de.uni_koblenz.jgralab.greql2.exception.FunctionInvalidIndexException;
import de.uni_koblenz.jgralab.greql2.exception.WrongFunctionParameterException;
import de.uni_koblenz.jgralab.greql2.jvalue.JValue;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueCollection;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueList;
import de.uni_koblenz.jgralab.greql2.jvalue.JValueTuple;

/**
 * Returns the n-th element of the given list or tuple.
 * 
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>OBJECT nthElement(list:LIST&lt;OBJECT&gt;, n:INTEGER)</code></dd>
 * <dd><code>OBJECT nthElement(tuple:TUPLE&lt;OBJECT&gt;, n:INTEGER)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>list</code> - list to return n-th element for</dd>
 * <dd><code>tuple</code> - tuple to return n-th element for</dd>
 * <dd><code>n</code> - index of the element to return</dd>
 * <dt><b>Returns:</b></dt>
 * <dd> the n-th element of the given list or tuple</dd>
 * <dd><code>Null</code> if one of the parameters is <code>Null</code></dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 * 
 */

/*
 * Calculates the n-th element of a list or tuple and returns it
 * 
 * @param structure a JValueList or JValueTuple to get the n. element for+
 * @param index the index of the element in the structure to access @return the
 * element with the given index or a invlaid JValue @throw a
 * FunctionInvalidIndexException if the given structure doesn't contain a
 * element with the given index, for instance if one provides a list [1,2,3] and
 * tries to acces list[17] @author ist@uni-koblenz.de
 * Summer 2006, Diploma Thesis
 * 
 */

public class NthElement implements Greql2Function {

	public JValue evaluate(Graph graph, BooleanGraphMarker subgraph,
			JValue[] arguments) throws EvaluateException {
		if (arguments.length < 2) {
			throw new WrongFunctionParameterException(this, null, arguments);
		}
		try {
			int index = arguments[1].toInteger();
			JValueCollection col = arguments[0].toCollection();
			if (index >= col.size()) {
				throw new FunctionInvalidIndexException(col.getClass()
						.getName(), index, null);
			}
			if (col.isJValueList()) {
				JValueList list = col.toJValueList();
				return list.get(index);
			}
			// TODO shouldn't the if-block above and below be swaped? Is the
			// block below necessary? Reason: JValueTuple is a specialisation of
			// JValueList!
			if (col.isJValueTuple()) {
				JValueTuple tup = col.toJValueTuple();
				return tup.get(index);
			}
			return new JValue();
		} catch (Exception ex) { // JValueInvalidTypeException,
			// NoSuchFieldException,
			// IndexOutOfBoundsException
			throw new WrongFunctionParameterException(this, null, arguments);
		}
	}

	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 2;
	}

	public double getSelectivity() {
		return 1;
	}

	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

	public String getExpectedParameters() {
		return "(JValueList or JValueTuple, integer )";
	}

	@Override
	public boolean isPredicate() {
		return false;
	}
}
