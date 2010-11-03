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

package de.uni_koblenz.jgralab;

/**
 * Specifies direction of edges for traversal methods. IN: Only incoming edges
 * are traversed, OUT: only outgoing edges are traversed, INOUT: both incoming
 * and outgoing edges are traversed.
 * 
 * @author ist@uni-koblenz.de
 */
public enum EdgeDirection {

	IN, OUT, INOUT;

	/**
	 * Parses a given string for edge direction.
	 * 
	 * @param direction
	 *            A string containing only IN, OUT, or INOUT.
	 * @return Matching edge direction.
	 * @throws Exception
	 *             When no edge direction could be matched from string.
	 */
	public static EdgeDirection parse(String direction) throws Exception {
		if (direction.equals("OUT"))
			return EdgeDirection.OUT;
		else if (direction.equals("IN"))
			return EdgeDirection.IN;
		else if (direction.equals("INOUT"))
			return EdgeDirection.INOUT;
		else
			throw new Exception("Could not determine direction from string.");
	}
}
