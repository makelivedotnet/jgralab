/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2014 Institute for Software Technology
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
package de.uni_koblenz.jgralab.graphmarker;

import java.util.Iterator;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.algolib.functions.LongFunction;
import de.uni_koblenz.jgralab.algolib.functions.entries.LongFunctionEntry;

public abstract class LongGraphMarker<T extends GraphElement<?, ?>> extends
		AbstractGraphMarker<T> implements LongFunction<T> {

	private static final long DEFAULT_UNMARKED_VALUE = Long.MIN_VALUE;

	protected long[] temporaryAttributes;
	protected int marked;
	protected long unmarkedValue;
	protected long version;

	protected LongGraphMarker(Graph graph, int size) {
		super(graph);
		unmarkedValue = DEFAULT_UNMARKED_VALUE;
		temporaryAttributes = createNewArray(size);
		marked = 0;
	}

	private long[] createNewArray(int size) {
		long[] newArray = new long[size];
		for (int i = 0; i < size; i++) {
			newArray[i] = unmarkedValue;
		}
		return newArray;
	}

	@Override
	public void clear() {
		for (int i = 0; i < temporaryAttributes.length; i++) {
			temporaryAttributes[i] = unmarkedValue;
		}
		marked = 0;
	}

	@Override
	public boolean isEmpty() {
		return marked == 0;
	}

	@Override
	public boolean isMarked(T graphElement) {
		assert (graphElement.getGraph() == graph);
		assert (graphElement.getId() <= (graphElement instanceof Vertex ? graph
				.getMaxVCount() : graph.getMaxECount()));
		return temporaryAttributes[graphElement.getId()] != unmarkedValue;
	}

	/**
	 * marks the given element with the given value
	 * 
	 * @param graphElement
	 *            the graph element to mark
	 * @param value
	 *            the object that should be used as marking
	 * @return The previous element the given graph element has been marked
	 *         with, <code>null</code> if the given element has not been marked.
	 */
	public long mark(T graphElement, long value) {
		assert (graphElement.getGraph() == graph);
		assert (graphElement.getId() <= (graphElement instanceof Vertex ? graph
				.getMaxVCount() : graph.getMaxECount()));
		long out = temporaryAttributes[graphElement.getId()];
		temporaryAttributes[graphElement.getId()] = value;
		marked += 1;
		version++;
		return out;
	}

	public long getMark(T graphElement) {
		assert (graphElement.getGraph() == graph);
		assert (graphElement.getId() <= (graphElement instanceof Vertex ? graph
				.getMaxVCount() : graph.getMaxECount()));
		long out = temporaryAttributes[graphElement.getId()];
		return out;
	}

	@Override
	public boolean removeMark(T graphElement) {
		assert (graphElement.getGraph() == graph);
		assert (graphElement.getId() <= (graphElement instanceof Vertex ? graph
				.getMaxVCount() : graph.getMaxECount()));
		if (temporaryAttributes[graphElement.getId()] == unmarkedValue) {
			return false;
		}
		temporaryAttributes[graphElement.getId()] = unmarkedValue;
		marked -= 1;
		version++;
		return true;
	}

	@Override
	public int size() {
		return marked;
	}

	public int maxSize() {
		return temporaryAttributes.length - 1;
	}

	protected void expand(int newSize) {
		assert (newSize > temporaryAttributes.length);
		long[] newTemporaryAttributes = createNewArray(newSize);
		System.arraycopy(temporaryAttributes, 0, newTemporaryAttributes, 0,
				temporaryAttributes.length);
		temporaryAttributes = newTemporaryAttributes;
	}

	public long getUnmarkedValue() {
		return unmarkedValue;
	}

	public void setUnmarkedValue(long newUnmarkedValue) {
		if (newUnmarkedValue != this.unmarkedValue) {
			for (int i = 0; i < temporaryAttributes.length; i++) {
				// keep track of implicitly unmarked values
				if (temporaryAttributes[i] == newUnmarkedValue) {
					marked -= 1;
				}
				// set all unmarked elements to new value
				if (temporaryAttributes[i] == this.unmarkedValue) {
					temporaryAttributes[i] = newUnmarkedValue;
				}

			}
			this.unmarkedValue = newUnmarkedValue;
		}
	}

	@Override
	public long get(T parameter) {
		return getMark(parameter);
	}

	@Override
	public boolean isDefined(T parameter) {
		return isMarked(parameter);
	}

	@Override
	public void set(T parameter, long value) {
		mark(parameter, value);
	}

	@Override
	public Iterable<T> getDomainElements() {
		return getMarkedElements();
	}

	@Override
	public Iterator<LongFunctionEntry<T>> iterator() {
		final Iterator<T> markedElements = getMarkedElements().iterator();
		return new Iterator<LongFunctionEntry<T>>() {

			@Override
			public boolean hasNext() {
				return markedElements.hasNext();
			}

			@Override
			public LongFunctionEntry<T> next() {
				T currentElement = markedElements.next();
				return new LongFunctionEntry<>(currentElement,
						get(currentElement));
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub

			}
		};
	}

}
