/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl.HasAttributeM2Impl;
/**
FromVertexClass: AttributeM2
FromRoleName : 
ToVertexClass: AttributedElementClassM2
toRoleName : 
 */

public interface HasAttributeM2 extends Aggregation {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<HasAttributeM2Impl> IMPLEMENTATION_CLASS = HasAttributeM2Impl.class;

	/**
	 * @return the next HasAttributeM2 edge in the global edge sequence
	 */
	public HasAttributeM2 getNextHasAttributeM2InGraph();

	/**
	 * @return the next HasAttributeM2 edge in the global edge sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasAttributeM2 are accepted
	 */
	public HasAttributeM2 getNextHasAttributeM2InGraph(boolean noSubClasses);

	/**
	 * @return the next edge of class HasAttributeM2 at the "this" vertex
	 */
	public HasAttributeM2 getNextHasAttributeM2();

	/**
	 * @return the next edge of class HasAttributeM2 at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public HasAttributeM2 getNextHasAttributeM2(EdgeDirection orientation);

	/**
	 * @return the next edge of class HasAttributeM2 at the "this" vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasAttributeM2 are accepted
	 */
	public HasAttributeM2 getNextHasAttributeM2(boolean noSubClasses);

	/**
	 * @return the next edge of class HasAttributeM2 at the "this" vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasAttributeM2 are accepted
	 */
	public HasAttributeM2 getNextHasAttributeM2(EdgeDirection orientation, boolean noSubClasses);

}
