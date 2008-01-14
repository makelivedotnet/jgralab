/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.utilities.tg2schemagraph.grumlschema.impl.HasBaseDomainM2Impl;
/**
FromVertexClass: CompositeDomainM2
FromRoleName : 
ToVertexClass: DomainM2
toRoleName : 
 */

public interface HasBaseDomainM2 extends Edge {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<HasBaseDomainM2Impl> IMPLEMENTATION_CLASS = HasBaseDomainM2Impl.class;

	/**
	 * @return the next HasBaseDomainM2 edge in the global edge sequence
	 */
	public HasBaseDomainM2 getNextHasBaseDomainM2InGraph();

	/**
	 * @return the next HasBaseDomainM2 edge in the global edge sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasBaseDomainM2 are accepted
	 */
	public HasBaseDomainM2 getNextHasBaseDomainM2InGraph(boolean noSubClasses);

	/**
	 * @return the next edge of class HasBaseDomainM2 at the "this" vertex
	 */
	public HasBaseDomainM2 getNextHasBaseDomainM2();

	/**
	 * @return the next edge of class HasBaseDomainM2 at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public HasBaseDomainM2 getNextHasBaseDomainM2(EdgeDirection orientation);

	/**
	 * @return the next edge of class HasBaseDomainM2 at the "this" vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasBaseDomainM2 are accepted
	 */
	public HasBaseDomainM2 getNextHasBaseDomainM2(boolean noSubClasses);

	/**
	 * @return the next edge of class HasBaseDomainM2 at the "this" vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of HasBaseDomainM2 are accepted
	 */
	public HasBaseDomainM2 getNextHasBaseDomainM2(EdgeDirection orientation, boolean noSubClasses);

}
